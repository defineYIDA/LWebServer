package com.lws.lwebserver.core.net.handler.bio;

import com.lws.lwebserver.core.context.ServletContext;
import com.lws.lwebserver.core.context.WebApplication;
import com.lws.lwebserver.core.exception.ServletNotFoundException;
import com.lws.lwebserver.core.exception.handler.ExceptionHandler;
import com.lws.lwebserver.core.exception.FilterNotFoundException;
import com.lws.lwebserver.core.net.handler.AbstractRequestHandler;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.net.wrapper.bio.BioSocketWrapper;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.resource.ResourceHandler;
import com.lws.lwebserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author: zl
 * @Date: 2019/3/20 0:18
 */
@Slf4j
public class BioRequestHandler extends AbstractRequestHandler {
    public BioRequestHandler(SocketWrapperBase socketWrapper, ServletContext servletContext, ExceptionHandler exceptionHandler, ResourceHandler resourceHandler, Request request, Response response) throws ServletNotFoundException, FilterNotFoundException {
        super(socketWrapper, servletContext, exceptionHandler, resourceHandler, request, response);
    }

    @Override
    public void flushResponse() {
        isFinished = true;
        BioSocketWrapper bioSocketWrapper = (BioSocketWrapper) socketWrapper;
        byte[] bytes = response.getResponseBytes();
        OutputStream os = null;
        try {
            os = bioSocketWrapper.getSocket().getOutputStream();
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("socket closed");
        } finally {
            try {
                if(os!=null){
                    os.close();
                }
                bioSocketWrapper.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        WebApplication.getServletContext().afterRequestDestroyed(request);
    }
}
