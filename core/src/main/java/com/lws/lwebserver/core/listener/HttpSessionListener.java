package com.lws.lwebserver.core.listener;

import com.lws.lwebserver.core.listener.event.HttpSessionEvent;

import java.util.EventListener;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:14
 */
public interface HttpSessionListener extends EventListener {
    /**
     * session创建
     * @param se
     */
    void sessionCreated(HttpSessionEvent se);

    /**
     * session销毁
     * @param se
     */
    void sessionDestroyed(HttpSessionEvent se);

}
