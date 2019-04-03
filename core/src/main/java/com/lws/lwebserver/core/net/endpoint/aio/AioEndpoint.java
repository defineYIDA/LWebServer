package com.lws.lwebserver.core.net.endpoint.aio;

import com.lws.lwebserver.core.net.base.SocketProcessorBase;
import com.lws.lwebserver.core.net.dispatcher.aio.AioDispatcher;
import com.lws.lwebserver.core.net.endpoint.AbstractEndpoint;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.net.wrapper.aio.AioSocketWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.*;

/**
 * @Author: zl
 * @Date: 2019/3/27 15:00
 */
@Slf4j
public class AioEndpoint extends AbstractEndpoint<AioSocketWrapper> {

    private AsynchronousServerSocketChannel serverSocket;//异步的主socket通道
    //private AioAcceptor aioAcceptor;
    private ThreadPoolExecutor pool;
    @Override
    public void start(int port) {
        running=true;//run endpoint
        paused=false;
        try {
            initDispatcher();
            initServerSocket(port);
            log.info("服务器启动");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("初始化服务器失败");
            close();
        }
    }

    private void initServerSocket(int port) throws IOException {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Endpoint Pool-" + count++);
            }
        };
        int processors = Runtime.getRuntime().availableProcessors();
        pool = new ThreadPoolExecutor(processors, processors, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
        // 以指定线程池来创建一个AsynchronousChannelGroup
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup
                .withThreadPool(pool);
        // 以指定线程池来创建一个AsynchronousServerSocketChannel
        serverSocket = AsynchronousServerSocketChannel.open(channelGroup)
                // 指定监听本机的PORT端口
                .bind(new InetSocketAddress(port));
        // 使用CompletionHandler接受来自客户端的连接请求
        //aioAcceptor = new AioAcceptor();
        // 开始接收客户端连接
        accept();
    }
    @Override
    protected void initDispatcher() {
        dispatcher=new AioDispatcher();
    }

    @Override
    protected SocketProcessorBase<AioSocketWrapper> createSocketProcessor(SocketWrapperBase<AioSocketWrapper> socketWrapper) {
        return null;
    }

    @Override
    public void close() {
        dispatcher.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 接收一个客户端连接
     */
    public void accept() {
        /**
         * 调用accept并且注册回调，这里方式有两种：
         * 1）挂起的	• Future<AsynchronousSocketChannel> accept();
         *2）注册回调的	• <A> void accept(Aattachment,CompletionHandler<AsynchronousSocketChannel,?superA>handler);
         * 并且两种方法都会产生一定的错误率，相对来说挂起的方式错误率低
         */
        //serverSocket.accept(null, aioAcceptor);
        startAcceptorThreads("AIO-Acceptor");
    }
    //-----------------------------------------------------acceptor start
    @Override
    protected Acceptor createAcceptor() {
        return new Acceptor();
    }

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
                AsynchronousSocketChannel  clientSocket =null;
                try {
                    //调用阻塞
                    clientSocket=serverSocket.accept().get();
                }catch (Exception e){
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
    private boolean setSocketOptions(AsynchronousSocketChannel socket) {
        return processSocket(new AioSocketWrapper(socket,this));
    }
    private void closeSocket(AsynchronousSocketChannel socket) {
        try {
            socket.close();
        } catch (IOException ioe) {
            if (log.isDebugEnabled()) {
                log.debug("endpoint.err.close", ioe);
            }
        }
    }
    /*    *//**
     * 执行读已就绪的客户端连接的请求
     * @param client
     *//*
    public void execute(AsynchronousSocketChannel client) {

        dispatcher.doDispatch(new AioSocketWrapper(client,this));
    }*/
/*    *//**
     * 回调的方式，注销掉，尝试挂起的方式
     *//*
    protected class AioAcceptor implements CompletionHandler<AsynchronousSocketChannel, Void> {
        @Override
        public void completed(AsynchronousSocketChannel client, Void attachment) {
            accept();
            execute(client);
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            log.info("accept failed...");
            exc.printStackTrace();
        }
    }*/
    //-----------------------------------------------------acceptor end
}
