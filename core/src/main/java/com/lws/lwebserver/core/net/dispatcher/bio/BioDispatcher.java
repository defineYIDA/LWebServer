package com.lws.lwebserver.core.net.dispatcher.bio;

import com.lws.lwebserver.core.exception.RequestInvalidException;
import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.net.dispatcher.AbstractDispatcher;
import com.lws.lwebserver.core.net.handler.bio.BioRequestHandler;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.net.wrapper.bio.BioSocketWrapper;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @Author: zl
 * @Date: 2019/3/20 0:14
 */
@Slf4j
public class BioDispatcher extends AbstractDispatcher {
    public BioDispatcher() {
        super();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public void doDispatch(SocketWrapperBase socketWrapper) {
        BioSocketWrapper bioSocketWrapper = (BioSocketWrapper) socketWrapper;
        Socket socket = bioSocketWrapper.getSocket();
        Request request = null;
        Response response = null;
        try {
            InputStream is=socket.getInputStream();
            BufferedInputStream bin = new BufferedInputStream(is);
            byte[] buf = null;
            try {
                buf = new byte[bin.available()];
                int len = bin.read(buf);
                if (len <= 0) {
                    throw new RequestInvalidException();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 这里这里不要把in关掉，把in关掉就等同于把socket关掉
            //解析请求
            response = new Response();
            request = new Request(buf);
            pool.execute(new BioRequestHandler(socketWrapper, servletContext, exceptionHandler, resourceHandler, request, response));
        } catch (ServletException e) {
            exceptionHandler.handle(e, response, socketWrapper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
