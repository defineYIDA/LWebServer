package com.lws.lwebserver.core.listener;

import com.lws.lwebserver.core.listener.event.ServletContextEvent;

import java.util.EventListener;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:15
 */
public interface ServletContextListener extends EventListener {
    /**
     * 应用启动
     * @param sce
     */
    void contextInitialized(ServletContextEvent sce);

    /**
     * 应用关闭
     * @param sce
     */
    void contextDestroyed(ServletContextEvent sce);
}
