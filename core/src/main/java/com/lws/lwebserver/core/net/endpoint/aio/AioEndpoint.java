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

    private AsynchronousServerSocketChannel serverSocket;//异步的主socket
    private AioAcceptor aioAcceptor;
    private ExecutorService pool;
    @Override
    public void start(int port) {
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
        aioAcceptor = new AioAcceptor();
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
        serverSocket.accept(null, aioAcceptor);
    }
    /**
     * 执行读已就绪的客户端连接的请求
     * @param client
     */
    public void execute(AsynchronousSocketChannel client) {

        dispatcher.doDispatch(new AioSocketWrapper(client,this));
    }
    //-----------------------------------------------------acceptor start
    @Override
    protected Acceptor createAcceptor() {
        return null;
    }

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
    }
    //-----------------------------------------------------acceptor end
}
