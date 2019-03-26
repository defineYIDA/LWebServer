package com.lws.lwebserver.core.servlet.impl;

import com.lws.lwebserver.core.enumeration.RequestMethod;
import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:25
 */@Slf4j
public class DefaultServlet extends HttpServlet {

    @Override
    public void service(Request request, Response response) throws ServletException, IOException {
        if (request.getMethod() == RequestMethod.GET) {
            //首页
            if (request.getUrl().equals("/")) {
                request.setUrl("/index.html");
            }
            request.getRequestDispatcher(request.getUrl()).forward(request, response);
        }
    }
}
