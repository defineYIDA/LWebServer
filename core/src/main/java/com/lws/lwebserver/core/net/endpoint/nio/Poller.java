package com.lws.lwebserver.core.net.endpoint.nio;

import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.net.wrapper.nio.NioSocketWrapper;
import com.lws.lwebserver.core.util.SynchronizedQueue;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zl
 * Poller class 轮询线程
 * 主要维护多路复用器和PollerEvent队列
 */
@Slf4j
public class Poller implements Runnable {

    private String pollerName;
    private Selector selector;
    private NioEndpoint endpoint;

    private final SynchronizedQueue<PollerEvent> events =
            new SynchronizedQueue<>();

    private volatile boolean close = false;

    private Map<SocketChannel, NioSocketWrapper> sockets;//所有活跃socket
    /**
     * 用来标识selector的状态，这里的设计方式极其巧妙！
     *         -1-------阻塞状态(被系统调用select()阻塞)
     *         0--------初始状态(select()返回后，回到初始状态)
     *      >0--------events存在没有被注册的socket，
     */
    private AtomicLong wakeupCounter = new AtomicLong(0);

    private volatile int keyCount = 0;

    private long selectorTimeout = 1000;

    public Selector getSelector() { return selector;}

    public Poller(NioEndpoint nioEndpoint,String pollerName) throws IOException {
        this.selector = Selector.open();//开启多路复用器
        this.endpoint=nioEndpoint;
        this.pollerName=pollerName;
        sockets=new ConcurrentHashMap<>();
    }

    /**
     * 结束Poller
     */
    protected void destroy() throws IOException {
        for (NioSocketWrapper wrapper : sockets.values()) {
            wrapper.close();
        }
        events.clear();
        close = true;//停止轮询
        selector.wakeup();
    }
    /**
     * 向selector中添加socket，后台线程检查poller中的触发事件，
     * 并将socket交给合适的进程进行处理。
     *
     */
    @Override
    public void run() {
        //Loop until destroy()
        while (true){
            boolean hasEvents=false;
            try {
                if(!close){// 未关闭
                    events();
                    if(wakeupCounter.getAndSet(-1)>0){
                        //代表此时events存在未注册的值，立即返回(执行一个non blocking select)
                        keyCount = selector.selectNow();
                    }else {
                        keyCount=selector.select(selectorTimeout);//设置阻塞超时时间
                    }
                    wakeupCounter.set(0);
                }
                if (close){//关闭
                    try {
                        selector.close();
                    } catch (IOException ioe) {
                        log.error("endpoint.nio.selectorCloseFail", ioe);
                    }
                    break;
                }
            }catch (Throwable e){
                log.error("",e);
                continue;
            }
            Iterator<SelectionKey> iterator =
                    keyCount > 0 ? selector.selectedKeys().iterator() : null;
            while (iterator != null && iterator.hasNext()){
                SelectionKey sk = iterator.next();
                /**
                 * 获得可操作对象
                 */
                NioSocketWrapper attachment = (NioSocketWrapper)sk.attachment();
                if(!sk.isReadable()){
                    iterator.remove();
                }
                if(null==attachment){
                    iterator.remove();
                }else {
                    iterator.remove();

                    /**粗略概况一下接下来的流程(Tomcat源码的流程，但是接下来我采用的是简化的流程)
                     *
                     *  |__交由processKey(SelectionKey sk, NioSocketWrapper attachment),判断endponit,attachment的各种状态，判断出对socket的操作SocketEvent，
                     *  这里还有对数据拷贝优化系统调用sendfile的支持；
                     *      |__processSocket(),创建处理的线程SocketProcess，放入线程池并且触发线程；
                     *          |__SocketProcessor.run,判断是否是SSL/TLS的请求: |___1) No TLS handshaking required. Let the handler process this socket / event combination.
                     *                                                                                         |___2) Unable to complete the TLS handshake. Treat it as if the handshake failed.
                     *                                                                                         |___3) The handshake process reads/writes from/to the socket. status may therefore be OPEN_WRITE once the handshake completes.
                     *                                                                                         However, the handshake happens when the socket is opened so the status must always be OPEN_READ after it completes.
                     *                                                                                         It is OK to always set this as it is only used if the handshake completes.
                     *                 上面状态1）请求不是TLS握手，则正常由Handler.process处理。
                     *                 |__ process,针对不同协议做相应的一系列处理(还包括对keep-alive的处理)(这里头皮发麻_(:з)∠)_)
                     *                      wrapper.registerReadInterest();//此方法对socket再次注册到poller中
                     *
                     */
                    processKey(sk, attachment);
                }
            }
        }
    }

    /**
     * 返回可处理的key
     * @param sk
     * @param attachment
     */
    protected void processKey(SelectionKey sk, NioSocketWrapper attachment){
        try {
            if(close){
                cancelledKey(sk);
            }else if(sk.isValid() && attachment != null ){
                processSocket(attachment);
            }else {
                cancelledKey(sk);
            }
        }catch (CancelledKeyException c){
            cancelledKey(sk);
        }catch (Throwable t){
            log.error("",t);
        }
    }
    private void processSocket(NioSocketWrapper attachment) {
        attachment.setWorking(true);
        //交由Excutor线程池处理
        endpoint.execute(attachment);
    }
    /**
     * 注册到PollerEvent
     * @param socketChannel
     * @param isNew
     */
    public void register(SocketChannel socketChannel, boolean isNew) {
        NioSocketWrapper socketWrapper;
        if(isNew){//is new socket
            socketWrapper=new NioSocketWrapper(socketChannel,endpoint,this);//包装socketchannel
            sockets.put(socketChannel,socketWrapper);//缓存住，用于管理socket
        }else {//keep-alive 长连接
            socketWrapper=sockets.get(socketChannel);
            socketWrapper.setWorking(false);
        }
        socketWrapper.setWaitBegin(System.currentTimeMillis());
        addEvent(new PollerEvent(socketWrapper));//注册到PollerEvent
    }

    private void addEvent(PollerEvent event) {
        events.offer(event);
        /**
         *某个线程调用select()方法后阻塞了，即使没有通道已经就绪，也有办法让其从select()方法返回。
         * 只要让其它线程在第一个线程调用select()方法的那个对象上调用Selector.wakeup()方法即可。
         * 阻塞在select()方法上的线程会立马返回,然后注册evens中的PollerEvent
         */
        if ( wakeupCounter.incrementAndGet() == 0 ) selector.wakeup();
    }

    /**
     * 将队列中的注册事件全部执行(注册到selector)，并且清空队列
     * @return
     */
    private boolean events(){
        boolean result=false;
        PollerEvent pe = null;
        /**
         * pop() 从此列表所表示的堆栈处弹出一个元素；
         * poll() 获取并移除此列表的头（第一个元素）；
         * 将队列中的注册事件全部执行，并且清空队列
         */
        for (int i = 0, size = events.size(); i < size && (pe = events.poll()) != null; i++ ) {
            result=true;
            pe.run();
            pe.reset();
            //TODO 这里tomcat里做了个缓存操作 eventCache
        }
        return result;
    }

    public NioSocketWrapper cancelledKey(SelectionKey key) {
        NioSocketWrapper ka=null;
        try {
            if(null==key){
                return null;
            }
            ka=(NioSocketWrapper) key.attach(null);
/*            if(null!=ka){

            }*/
            if(key.isValid()){
                key.cancel();
            }
            if(null!=ka){
                try {
                    ka.getSocket().close();
                }catch (Exception e){
                    log.info("endpoint.debug.socketCloseFail"+e);
                }
            }
            if(key.channel().isOpen()){
                try {
                    key.channel().close();
                }catch (Exception e){
                    log.info("endpoint.debug.channelCloseFail"+e);
                }
            }
        }catch (Throwable e){
            if (log.isDebugEnabled()) log.error("",e);
        }
        return ka;
    }
    //-----------------------------------------------------PollerEvent start
    /**
     *Cacheable object for poller events to avoid GC
     * 缓存轮询事件，避免GC
     * 并将事件在合适时机注册到Selector中
     * 注意这里涉及两种注册：
     *        1)accept()到客户机的socketchannel后注册到PollerEvent队列(events)；
     *        2)然后轮询线程(Poller),在每一次轮询之前，调用events(),将所有PollerEvent，注册到Selector.
     */
    private static class PollerEvent implements Runnable{

        private NioSocketWrapper socketWrapper;

        public PollerEvent(NioSocketWrapper socketWrapper) {
            reset(socketWrapper);
        }
        public void reset(NioSocketWrapper w) {
            socketWrapper = w;
        }
        public void reset() {
            reset(null);
        }
        @Override
        public void run() {
            log.info("将SocketChannel的读事件注册到Poller的selector中");
            try {
                if (socketWrapper.getSocketChannel().isOpen()) {
                    /**注册并且标记当前服务的通道状态
                     * register(Selector,int)
                     * int---状态编码
                     *     OP_READ： 可读标记位
                     *     OP_WRITE： 可写标记位
                     *     OP_CONNECT： 连接建立后的标记
                     *     OP_ACCEPT： 连接成功的标记位
                     */
                    socketWrapper.getSocketChannel().register(socketWrapper.getPoller().getSelector(), SelectionKey.OP_READ, socketWrapper);
                } else {
                    log.error("socket已经被关闭，无法注册到Poller", socketWrapper.getSocketChannel());
                }
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }
    //-----------------------------------------------------PollerEvent end
}
