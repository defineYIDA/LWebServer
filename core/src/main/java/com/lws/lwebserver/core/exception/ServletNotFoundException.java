package com.lws.lwebserver.core.exception;

import com.lws.lwebserver.core.enumeration.HTTPStatus;
import com.lws.lwebserver.core.exception.base.ServletException;

/**
 * Created by zl on 2019/03/01.
 */
public class ServletNotFoundException extends ServletException {
    private static final HTTPStatus status = HTTPStatus.NOT_FOUND;
    public ServletNotFoundException() {
        super(status);
    }
}
