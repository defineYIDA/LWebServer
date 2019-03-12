package com.lws.lwebserver.core.net.endpoint.nio;

import com.lws.lwebserver.core.net.wrapper.nio.NioSocketWrapper;
import com.lws.lwebserver.core.util.SynchronizedQueue;

import java.nio.channels.Selector;

/**
 * @author zl
 * Poller class 轮询线程
 * 主要维护多路复用器和PollerEvent队列
 */
public class Poller implements Runnable {
    private Selector selector;

    private final SynchronizedQueue<PollerEvent> events =
            new SynchronizedQueue<>();





    @Override
    public void run() {

    }

    //-----------------------------------------------------PollerEvent start
    /**
     *cacheable object for poller events to avoid GC
     * 缓存轮询事件，避免GC
     */
    private static class PollerEvent implements Runnable{

        private NioSocketWrapper socketWrapper;

        public PollerEvent(NioSocketWrapper socketWrapper) {
            this.socketWrapper = socketWrapper;
        }

        @Override
        public void run() {

        }
    }
    //-----------------------------------------------------PollerEvent end
}
