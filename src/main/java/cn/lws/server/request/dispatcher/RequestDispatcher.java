package cn.lws.server.request.dispatcher;

import cn.lws.server.exception.base.ServletException;
import cn.lws.server.request.Request;
import cn.lws.server.response.Response;

import java.io.IOException;

/**
 * Created by zl on 2019/03/01.
 */
public interface RequestDispatcher {
    
    void forward(Request request, Response response)  throws ServletException, IOException;
}
