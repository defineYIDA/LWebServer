package com.lws.lwebserver.core.servlet;

import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;

import java.io.IOException;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:24
 */
public interface Servlet {
    void init();

    void destroy();

    void service(Request request, Response response) throws ServletException, IOException;
}
