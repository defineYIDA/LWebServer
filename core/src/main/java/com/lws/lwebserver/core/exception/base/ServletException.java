package com.lws.lwebserver.core.exception.base;

import com.lws.lwebserver.core.enumeration.HttpStatus;
import lombok.Getter;

/**
 * Created by zl on 2019/03/01.
 */
@Getter
public class ServletException extends Exception {
    private HttpStatus status;
    public ServletException(HttpStatus status){
        this.status = status;
    }
}
