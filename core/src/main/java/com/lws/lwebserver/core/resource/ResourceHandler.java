package com.lws.lwebserver.core.resource;

import com.lws.lwebserver.core.constant.CharsetProperties;
import com.lws.lwebserver.core.exception.RequestParseException;
import com.lws.lwebserver.core.exception.ResourceNotFoundException;
import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.exception.handler.ExceptionHandler;
import com.lws.lwebserver.core.net.wrapper.nio.NioSocketWrapper;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;
import com.lws.lwebserver.core.template.TemplateResolver;
import lombok.extern.slf4j.Slf4j;
import com.lws.lwebserver.core.util.IOUtil;
import com.lws.lwebserver.core.util.MimeTypeUtil;

import java.io.IOException;

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

    public void handle(Request request, Response response, NioSocketWrapper socketWrapper) {
        String url = request.getUrl();
        try {
            if (ResourceHandler.class.getResource(url) == null) {
                log.info("找不到该资源:{}", url);
                throw new ResourceNotFoundException();
            }
            byte[] body = IOUtil.getBytesFromFile(url);
            if (url.endsWith(".html")) {
                body = TemplateResolver
                        .resolve(new String(body, CharsetProperties.UTF_8_CHARSET), request)
                        .getBytes(CharsetProperties.UTF_8_CHARSET);
            }
            response.setContentType(MimeTypeUtil.getTypes(url));
            response.setBody(body);
        } catch (IOException e) {
            exceptionHandler.handle(new RequestParseException(), response, socketWrapper);
        } catch (ServletException e) {
            exceptionHandler.handle(e, response, socketWrapper);
        }
    }
}