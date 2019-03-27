package com.lws.lwebserver.core.net.cleaner;

import com.lws.lwebserver.core.net.endpoint.nio.Poller;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zl
 * @Date: 2019/3/27 15:23
 */
@Slf4j
public class IdleConnectionCleaner implements Runnable {
    private ScheduledExecutorService executor;
    private Poller[] nioPollers;

    public IdleConnectionCleaner(Poller[] nioPollers) {
        this.nioPollers = nioPollers;
    }

    public void start() {
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "IdleConnectionCleaner");
            }
        };
        executor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        executor.scheduleWithFixedDelay(this, 0, 10, TimeUnit.SECONDS);
    }
    @Override
    public void run() {
        for (Poller nioPoller : nioPollers) {
            nioPoller.cleanTimeoutSockets();
        }
    }
}
