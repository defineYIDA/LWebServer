package com.lws.lwebserver.core.net.endpoint.nio;

import com.lws.lwebserver.core.net.base.SocketProcessorBase;
import com.lws.lwebserver.core.net.cleaner.IdleConnectionCleaner;
import com.lws.lwebserver.core.net.dispatcher.AbstractDispatcher;
import com.lws.lwebserver.core.net.dispatcher.nio.NioDispatcher;
import com.lws.lwebserver.core.net.endpoint.AbstractEndpoint;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.net.wrapper.nio.NioSocketWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zl
 * nio 模式切入点
 */
@Slf4j
public class NioEndpoint extends AbstractEndpoint<NioSocketWrapper> {

    private ServerSocketChannel serverSocket;//服务通道
    /**
     * 针对keep-alive连接，定时清理poller中的socket
     */
    private IdleConnectionCleaner cleaner;
    /**
     * 长连接超时时间
     */
    private int keepAliveTimeout = 6 * 1000 ;

    @Override
    public void start(int port) {
        try {
            running=true;//run endpoint
            paused=false;
            initDispatcher();
            initServerSocket(port);
            initPoller();
            startAcceptorThreads("NIO-Acceptor");
            initIdleSocketCleaner();//定时清理
        }catch (Exception e){
            e.printStackTrace();
            log.info("初始化服务器失败");
            close();
        }
    }

    @Override
    public void close() {
        running=false;
        for (Poller nioPoller : nioPollers) {
            try {
                nioPoller.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dispatcher.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initServerSocket(int port) throws IOException {
        serverSocket = ServerSocketChannel.open();//开启服务通道
        serverSocket.bind(new InetSocketAddress(port));//绑定端口号，使用到InetSocketAddress对象
        serverSocket.configureBlocking(true);//阻塞型io
    }
    @Override
    protected void initDispatcher() {
        dispatcher=new NioDispatcher();
    }
    /**
     * 初始化IdleSocketCleaner
     */
    private void initIdleSocketCleaner() {
        cleaner = new IdleConnectionCleaner(nioPollers);
        cleaner.start();
    }
    /**
     *处理获得客户机连接
     * 设置阻塞类型；
     * 注册到poller
     * @param socket
     * @return
     */
    protected boolean setSocketOptions(SocketChannel socket) {
        try {
            //注意监听serversocketchannel为阻塞，客户机连接为非阻塞
            socket.configureBlocking(false);
            //TODO SecureNioChannel SSL 对buffer进行加密
            getPoller().register(socket,true);//注册到pollerevent
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 关闭通道
     * @param socket
     */
    private void closeSocket(SocketChannel socket) {
        try {
            socket.socket().close();
        } catch (IOException ioe)  {
            if (log.isDebugEnabled()) {
                log.error("endpoint.err.close");
            }
        }
        try {
            socket.close();
        } catch (IOException ioe) {
            if (log.isDebugEnabled()) {
                log.error("endpoint.err.close");
            }
        }
    }

    /**
     *
     * @param socketWrapper
     */
    public void execute(NioSocketWrapper socketWrapper) {
        //TODO
    }
    public int getKeepAliveTimeout() {
        return keepAliveTimeout;
    }
    @Override
    protected SocketProcessorBase<NioSocketWrapper> createSocketProcessor(SocketWrapperBase<NioSocketWrapper> socketWrapper) {
        return new SocketProcessor(socketWrapper);
    }

    //-----------------------------------------------------Poller start
    /**
     * The socket poller.
     */
    private int pollerThreadCount = Math.min(2,Runtime.getRuntime().availableProcessors());//轮询池的数量，一般为cpu个数
    private Poller[] nioPollers = null;
    private AtomicInteger pollerRotater = new AtomicInteger(0);

    /**
     * 返回可用selector
     * @return The next poller in sequence
     */
    public Poller getPoller() {
        int idx = Math.abs(pollerRotater.incrementAndGet()) % nioPollers.length;
        return nioPollers[idx];
    }

    /**
     * Init poller
     * @throws IOException
     */
    private void initPoller() throws IOException {
        nioPollers = new Poller[pollerThreadCount];
        for (int i = 0; i < pollerThreadCount; i++) {
            String pollName="NIOPoller-"+i;
            nioPollers[i] = new Poller(this,pollName);
            Thread pollerThread = new Thread(nioPollers[i],  "LWS-ClientPoller-"+i);
            pollerThread.setPriority(Thread.NORM_PRIORITY);//设置优先级
            pollerThread.setDaemon(true);
            pollerThread.start();
        }
    }
    //-----------------------------------------------------Poller end

    //-----------------------------------------------------acceptor start
    @Override
    protected Acceptor createAcceptor() {
        return new Acceptor();
    }
    /**
     * 后台线程监听客户机的TCP连接
     */
    protected class Acceptor extends AbstractEndpoint.Acceptor{
        @Override
        public void run() {
            log.info("NIO Acceptor 开始监听");
            while (running){
                //endpoint阻塞
                while (paused && running) {
                    state = AcceptorState.PAUSED;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
                if(!running){
                    break;
                }
                state=AcceptorState.RUNNING;
                //TODO 待添加最大连接数的判断 LimitLatch
                SocketChannel  clientSocket =null;
                try {
                    //调用阻塞
                    clientSocket=serverSocket.accept();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(null==clientSocket){
                    continue;
                }
                if(running&&!paused){
                    if(!setSocketOptions(clientSocket)){
                        closeSocket(clientSocket);
                    }
                }else {
                   closeSocket(clientSocket);
                }

            }
        }
    }
    //-----------------------------------------------------acceptor end

    //-----------------------------------------------------SocketProcessor start
    protected class SocketProcessor extends SocketProcessorBase<NioSocketWrapper> {
        public SocketProcessor(SocketWrapperBase<NioSocketWrapper> socketWrapper) {
            super(socketWrapper);
        }

        @Override
        public void reset(SocketWrapperBase<NioSocketWrapper> socketWrapper) {
            super.reset(socketWrapper);
        }

        @Override
        protected void doRun() {
            //TODO
        }
    }
    //-----------------------------------------------------SocketProcessor end


}
