package com.lws.lwebserver.example.web.listener;

import com.lws.lwebserver.core.listener.ServletRequestListener;
import com.lws.lwebserver.core.listener.event.ServletRequestEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zl
 */
@Slf4j
public class MyServletRequestListener implements ServletRequestListener {

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        log.info("request destroy...");
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        log.info("request init...");
    }
}
