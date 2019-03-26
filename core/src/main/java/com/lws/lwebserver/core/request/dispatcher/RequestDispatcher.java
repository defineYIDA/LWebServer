package com.lws.lwebserver.core.request.dispatcher;

import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;

import java.io.IOException;

/**
 * Created by zl on 2019/03/01.
 */
public interface RequestDispatcher {
    
    void forward(Request request, Response response)  throws ServletException, IOException;
}
