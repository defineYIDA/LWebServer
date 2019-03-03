package cn.lws.server;

import cn.lws.server.servlet.context.ServletContext;

/**
 * Created by zl on 2019/03/01
 * singleton模式
 */
public class WebApplication {
    private static ServletContext servletContext;

    static {
        servletContext = new ServletContext();
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }

}
