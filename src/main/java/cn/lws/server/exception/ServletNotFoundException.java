package cn.lws.server.exception;

import cn.lws.server.exception.base.ServletException;
import cn.lws.server.enumeration.HTTPStatus;

/**
 * Created by zl on 2019/03/01.
 */
public class ServletNotFoundException extends ServletException {
    private static final HTTPStatus status = HTTPStatus.NOT_FOUND;
    public ServletNotFoundException() {
        super(status);
    }
}
