package com.lws.lwebserver.core.request.dispatcher.impl;

import com.lws.lwebserver.core.constant.CharsetProperties;
import com.lws.lwebserver.core.exception.ResourceNotFoundException;
import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.request.dispatcher.RequestDispatcher;
import com.lws.lwebserver.core.resource.ResourceHandler;
import com.lws.lwebserver.core.response.Response;
import com.lws.lwebserver.core.template.TemplateResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.lws.lwebserver.core.util.IOUtil;
import com.lws.lwebserver.core.util.MimeTypeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created by zl on 2019/03/01.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ApplicationRequestDispatcher implements RequestDispatcher {
    private String url;

    @Override
    public void forward(Request request, Response response) throws ServletException, IOException {
        if (ResourceHandler.class.getResource(url) == null) {
            throw new ResourceNotFoundException();
        }
        log.info("forward至 {} 页面",url);
        String body = TemplateResolver.resolve(new String(IOUtil.getBytesFromFile(url), CharsetProperties.UTF_8_CHARSET),request);
        response.setContentType(MimeTypeUtil.getTypes(url));
        response.setBody(body.getBytes(CharsetProperties.UTF_8_CHARSET));
    }
}
