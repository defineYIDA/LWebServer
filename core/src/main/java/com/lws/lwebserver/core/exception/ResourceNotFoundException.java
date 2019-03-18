package com.lws.lwebserver.core.exception;

import com.lws.lwebserver.core.enumeration.HttpStatus;
import com.lws.lwebserver.core.exception.base.ServletException;

/**
 * Created by zl on 2019/03/01.
 */
public class ResourceNotFoundException extends ServletException {
    private static final HttpStatus status = HttpStatus.NOT_FOUND;
    public ResourceNotFoundException() {
        super(status);
    }
}
