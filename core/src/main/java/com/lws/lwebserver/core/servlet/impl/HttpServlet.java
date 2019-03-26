package com.lws.lwebserver.core.servlet.impl;

import com.lws.lwebserver.core.enumeration.RequestMethod;
import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;
import com.lws.lwebserver.core.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:26
 */
@Slf4j
public abstract class HttpServlet implements Servlet {

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    public void service(Request request, Response response) throws ServletException, IOException {
        if (request.getMethod() == RequestMethod.GET) {
            doGet(request, response);
        } else if (request.getMethod() == RequestMethod.POST) {
            doPost(request, response);
        } else if (request.getMethod() == RequestMethod.PUT) {
            doPut(request, response);
        } else if (request.getMethod() == RequestMethod.DELETE) {
            doDelete(request, response);
        }
    }

    public void doGet(Request request, Response response) throws ServletException, IOException {
    }

    public void doPost(Request request, Response response) throws ServletException, IOException {
    }

    public void doPut(Request request, Response response) throws ServletException, IOException {
    }

    public void doDelete(Request request, Response response) throws ServletException, IOException {
    }


}
