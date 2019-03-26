package com.lws.lwebserver.core.listener.event;

import com.lws.lwebserver.core.context.ServletContext;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:17
 */
public class ServletContextEvent extends java.util.EventObject {


    public ServletContextEvent(ServletContext source) {
        super(source);
    }

    public ServletContext getServletContext () {
        return (ServletContext) super.getSource();
    }
}
