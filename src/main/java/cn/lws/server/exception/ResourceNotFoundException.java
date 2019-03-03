package cn.lws.server.exception;

import cn.lws.server.enumeration.HTTPStatus;
import cn.lws.server.exception.base.ServletException;

/**
 * Created by zl on 2019/03/01.
 */
public class ResourceNotFoundException extends ServletException {
    private static final HTTPStatus status = HTTPStatus.NOT_FOUND;
    public ResourceNotFoundException() {
        super(status);
    }
}
