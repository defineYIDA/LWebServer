package com.lws.lwebserver.core.request.dispatcher.impl;

import com.lws.lwebserver.core.constant.CharsetProperties;
import com.lws.lwebserver.core.enumeration.HTTPStatus;
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

import java.io.IOException;

/**
 * Created by zl on 2019/03/01.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequestDispatcher implements RequestDispatcher {
    private String url;
    
    @Override
    public void forward(Request request, Response response) throws ServletException, IOException {
        if (ResourceHandler.class.getResource(url) == null) {
            throw new ResourceNotFoundException();
        }
        String body = TemplateResolver.resolve(new String(IOUtil.getBytesFromFile(url), CharsetProperties.UTF_8_CHARSET),request);//模板赋值
        response.header(HTTPStatus.OK, MimeTypeUtil.getTypes(url)).body(body.getBytes(CharsetProperties.UTF_8_CHARSET));
    }
}
