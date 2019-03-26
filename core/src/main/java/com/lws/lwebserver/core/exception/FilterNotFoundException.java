package com.lws.lwebserver.core.exception;

import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.enumeration.HttpStatus;
/**
 * @Author: zl
 * @Date: 2019/3/16 16:36
 */
public class FilterNotFoundException extends ServletException {
    private static final HttpStatus status = HttpStatus.NOT_FOUND;
    public FilterNotFoundException() {
        super(status);
    }
}

