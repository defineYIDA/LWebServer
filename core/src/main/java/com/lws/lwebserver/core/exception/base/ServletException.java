package com.lws.lwebserver.core.exception.base;

import com.lws.lwebserver.core.enumeration.HTTPStatus;
import lombok.Getter;

/**
 * Created by zl on 2019/03/01.
 */
@Getter
public class ServletException extends Exception {
    private HTTPStatus status;
    public ServletException(HTTPStatus status){
        this.status = status;
    }
}
