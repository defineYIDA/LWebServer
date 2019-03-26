package com.lws.lwebserver.core.listener.event;

import com.lws.lwebserver.core.context.ServletContext;
import com.lws.lwebserver.core.request.Request;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:18
 */
public class ServletRequestEvent extends java.util.EventObject {

    private static final long serialVersionUID = -7467864054698729101L;

    private final transient Request request;


    public ServletRequestEvent(ServletContext sc, Request request) {
        super(sc);
        this.request = request;
    }


    public Request getServletRequest () {
        return this.request;
    }


    public ServletContext getServletContext () {
        return (ServletContext) super.getSource();
    }
}
