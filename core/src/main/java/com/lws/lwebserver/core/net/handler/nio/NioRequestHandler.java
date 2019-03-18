package com.lws.lwebserver.core.net.handler.nio;

import com.lws.lwebserver.core.context.ServletContext;
import com.lws.lwebserver.core.context.WebApplication;
import com.lws.lwebserver.core.exception.ServletNotFoundException;
import com.lws.lwebserver.core.exception.handler.ExceptionHandler;
import com.lws.lwebserver.core.exception.handler.FilterNotFoundException;
import com.lws.lwebserver.core.net.handler.AbstractRequestHandler;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.net.wrapper.nio.NioSocketWrapper;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.resource.ResourceHandler;
import com.lws.lwebserver.core.response.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @Author: zl
 * @Date: 2019/3/17 23:42
 */
@Setter
@Getter
@Slf4j
public class NioRequestHandler extends AbstractRequestHandler {

    public NioRequestHandler(SocketWrapperBase socketWrapper, ServletContext servletContext, ExceptionHandler exceptionHandler, ResourceHandler resourceHandler, Request request, Response response) throws ServletNotFoundException, FilterNotFoundException {
        super(socketWrapper, servletContext, exceptionHandler, resourceHandler, request, response);
    }

    /**
     * 写入后会根据请求头Connection来判断是关闭连接还是重新将连接放回Poller，实现保活
     */
    @Override
    public void flushResponse() {
        isFinished = true;
        NioSocketWrapper nioSocketWrapper = (NioSocketWrapper) socketWrapper;
        ByteBuffer[] responseData = response.getResponseByteBuffer();
        try {
            nioSocketWrapper.getSocket().write(responseData);
            List<String> connection = request.getHeaders().get("Connection");
            if (connection != null && connection.get(0).equals("close")) {
                log.info("CLOSE: 客户端连接{} 已关闭", nioSocketWrapper.getSocket());
                nioSocketWrapper.close();
            } else {
                // keep-alive 重新注册到Poller中
                log.info("KEEP-ALIVE: 客户端连接{} 重新注册到Poller中", nioSocketWrapper.getSocket());
                nioSocketWrapper.getPoller().register(nioSocketWrapper.getSocket(), false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        WebApplication.getServletContext().afterRequestDestroyed(request);
    }
}

