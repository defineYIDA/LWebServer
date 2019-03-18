package com.lws.lwebserver.core.context;

/**
 * @Author: zl
 * @Date: 2019/3/16 11:48
 */
public class WebApplication {
    private static ServletContext servletContext;

    static {
        try {
            servletContext = new ServletContext();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }
}
