package com.lws.lwebserver.core.listener.event;

import com.lws.lwebserver.core.session.HttpSession;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:17
 */
public class HttpSessionEvent extends java.util.EventObject {

    private static final long serialVersionUID = -7622791603672342895L;


    public HttpSessionEvent(HttpSession source) {
        super(source);
    }

    public HttpSession getSession () {
        return (HttpSession) super.getSource();
    }
}
