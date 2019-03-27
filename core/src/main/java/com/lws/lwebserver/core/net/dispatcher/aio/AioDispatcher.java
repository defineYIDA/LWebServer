package com.lws.lwebserver.core.net.dispatcher.aio;

import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.net.dispatcher.AbstractDispatcher;
import com.lws.lwebserver.core.net.handler.aio.AioRequestHandler;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.net.wrapper.aio.AioSocketWrapper;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * @Author: zl
 * @Date: 2019/3/27 15:00
 */
@Slf4j
public class AioDispatcher extends AbstractDispatcher {
    public AioDispatcher() {
        super();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public void doDispatch(SocketWrapperBase socketWrapper) {
        AioSocketWrapper aioSocketWrapper = (AioSocketWrapper) socketWrapper;
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        aioSocketWrapper.getSocket().read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                Request request = null;
                Response response = null;
                try {
                    //解析请求
                    request = new Request(attachment.array());
                    response = new Response();
                    pool.execute(new AioRequestHandler(aioSocketWrapper, servletContext, exceptionHandler, resourceHandler, request, response,this));
                } catch (ServletException e) {
                    exceptionHandler.handle(e, response, aioSocketWrapper);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable e, ByteBuffer attachment) {
                log.error("read failed");
                e.printStackTrace();
            }
        });
    }
}
