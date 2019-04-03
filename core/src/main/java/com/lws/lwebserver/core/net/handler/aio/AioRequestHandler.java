package com.lws.lwebserver.core.net.handler.aio;

import com.lws.lwebserver.core.context.ServletContext;
import com.lws.lwebserver.core.context.WebApplication;
import com.lws.lwebserver.core.exception.FilterNotFoundException;
import com.lws.lwebserver.core.exception.ServletNotFoundException;
import com.lws.lwebserver.core.exception.handler.ExceptionHandler;
import com.lws.lwebserver.core.net.handler.AbstractRequestHandler;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.net.wrapper.aio.AioSocketWrapper;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.resource.ResourceHandler;
import com.lws.lwebserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zl
 * @Date: 2019/3/27 15:01
 */
@Slf4j
public class AioRequestHandler extends AbstractRequestHandler {
    private CompletionHandler readHandler;
    public AioRequestHandler(SocketWrapperBase socketWrapper,
                             ServletContext servletContext,
                             ExceptionHandler exceptionHandler,
                             ResourceHandler resourceHandler,
                             Request request, Response response,
                             CompletionHandler readHandler) throws ServletNotFoundException, FilterNotFoundException
    {
        super(socketWrapper, servletContext, exceptionHandler, resourceHandler, request, response);
        this.readHandler=readHandler;
    }

    @Override
    public void flushResponse() {
        isFinished = true;
        ByteBuffer[] responseData = response.getResponseByteBuffer();
        AioSocketWrapper aioSocketWrapper = (AioSocketWrapper) socketWrapper;
        AsynchronousSocketChannel socketChannel = aioSocketWrapper.getSocket();
        /**
         * TODO 含义
         */
        socketChannel.write(responseData, 0, 2, 0L, TimeUnit.MILLISECONDS, null, new CompletionHandler<Long, Object>() {

            @Override
            public void completed(Long result, Object attachment) {
                log.info("写入完毕...");
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                socketChannel.read(byteBuffer, byteBuffer, readHandler);
            }

            @Override
            public void failed(Throwable e, Object attachment) {
                log.info("写入失败...");
                e.printStackTrace();
            }
        });
        WebApplication.getServletContext().afterRequestDestroyed(request);
    }
}
