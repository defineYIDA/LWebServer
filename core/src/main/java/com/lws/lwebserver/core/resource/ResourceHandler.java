package com.lws.lwebserver.core.resource;

import com.lws.lwebserver.core.enumeration.HTTPStatus;
import com.lws.lwebserver.core.exception.RequestParseException;
import com.lws.lwebserver.core.exception.ResourceNotFoundException;
import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.exception.handler.ExceptionHandler;
import com.lws.lwebserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;
import com.lws.lwebserver.core.util.IOUtil;
import com.lws.lwebserver.core.util.MimeTypeUtil;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by zl on 2019/03/01.
 * 处理静态资源
 */
@Slf4j
public class ResourceHandler {
    private ExceptionHandler exceptionHandler;

    public ResourceHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
    
    public void handle(String url, Response response, Socket client) {
        try {
            if (ResourceHandler.class.getResource(url) == null) {
                log.info("找不到该资源:{}",url);
                throw new ResourceNotFoundException();
            }
            response.header(HTTPStatus.OK, MimeTypeUtil.getTypes(url)).body(IOUtil.getBytesFromFile(url)).write();
            log.info("{}已写入输出流", url);
        } catch (IOException e) {
            e.printStackTrace();
            exceptionHandler.handle(new RequestParseException(), response, client);
        } catch (ServletException e) {
            exceptionHandler.handle(e, response, client);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}