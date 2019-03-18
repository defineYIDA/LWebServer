package com.lws.lwebserver.core.exception;

import com.lws.lwebserver.core.enumeration.HttpStatus;
import com.lws.lwebserver.core.exception.base.ServletException;

/**
 * Created by zl on 2019/03/01.
 */
public class TemplateResolveException extends ServletException {
    private static final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    public TemplateResolveException() {
        super(status);
    }
}
   
