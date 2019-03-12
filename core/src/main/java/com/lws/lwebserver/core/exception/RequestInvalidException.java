package com.lws.lwebserver.core.exception;

import com.lws.lwebserver.core.enumeration.HTTPStatus;
import com.lws.lwebserver.core.exception.base.ServletException;

/**
 * Created by zl on 2019/03/01.
 */
public class RequestInvalidException extends ServletException {
    private static final HTTPStatus status = HTTPStatus.BAD_REQUEST;
    public RequestInvalidException() {
        super(status);
    }
}
