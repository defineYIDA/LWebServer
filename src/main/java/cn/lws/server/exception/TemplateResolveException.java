package cn.lws.server.exception;

import cn.lws.server.exception.base.ServletException;
import cn.lws.server.enumeration.HTTPStatus;

/**
 * Created by zl on 2019/03/01.
 */
public class TemplateResolveException extends ServletException {
    private static final HTTPStatus status = HTTPStatus.INTERNAL_SERVER_ERROR;
    public TemplateResolveException() {
        super(status);
    }
}
   
