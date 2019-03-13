package com.lws.lwebserver.core.net.endpoint.nio;

import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.net.wrapper.nio.NioSocketWrapper;
import com.lws.lwebserver.core.util.SynchronizedQueue;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author zl
 * Poller class 轮询线程
 * 主要维护多路复用器和PollerEvent队列
 */
@Slf4j
public class Poller implements Runnable {

    private Selector selector;

    private final SynchronizedQueue<PollerEvent> events =
            new SynchronizedQueue<>();


    public Selector getSelector() { return selector;}

    public Poller() throws IOException {
        this.selector = Selector.open();//开启多路复用器
    }

    @Override
    public void run() {

    }

    /**
     * zhuc
     * @param socketChannel
     * @param isNew
     */
    public void register(SocketChannel socketChannel, boolean isNew) {
        //TODO
    }
    //-----------------------------------------------------PollerEvent start
    /**
     *cacheable object for poller events to avoid GC
     * 缓存轮询事件，避免GC
     * 并将事件在合适时机注册到Selector中
     * 注意有两种注册：
     *        1)accept()到客户机的socketchannel后注册到PollerEvent队列(events)；
     *        2)然后轮询线程(Poller),在每一次轮询之前，调用events,将所有PollerEvent，注册到Selector.
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
