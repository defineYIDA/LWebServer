package cn.lws.server.request.dispatcher.impl;

import cn.lws.server.constant.CharsetProperties;
import cn.lws.server.enumeration.HTTPStatus;
import cn.lws.server.exception.ResourceNotFoundException;
import cn.lws.server.exception.base.ServletException;
import cn.lws.server.request.Request;
import cn.lws.server.request.dispatcher.RequestDispatcher;
import cn.lws.server.resource.ResourceHandler;
import cn.lws.server.response.Response;
import cn.lws.server.template.TemplateResolver;
import cn.lws.server.util.IOUtil;
import cn.lws.server.util.MimeTypeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
