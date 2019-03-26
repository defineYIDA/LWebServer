package com.lws.lwebserver.core.listener;

import com.lws.lwebserver.core.listener.event.ServletRequestEvent;

import java.util.EventListener;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:15
 */
public interface ServletRequestListener extends EventListener {
    /**
     * 请求初始化
     * @param sre
     */
    void requestInitialized(ServletRequestEvent sre);

    /**
     * 请求销毁
     * @param sre
     */
    void requestDestroyed(ServletRequestEvent sre);
}
