package cn.lws.server.servlet.base;

import cn.lws.server.enumeration.RequestMethod;
import cn.lws.server.exception.base.ServletException;
import cn.lws.server.response.Response;
import cn.lws.server.request.Request;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created by zl on 2019/03/01
 *
 */
@Slf4j
public abstract class HTTPServlet {
    public void service(Request request, Response response) throws ServletException, IOException {
        if (request.getMethod() == RequestMethod.GET) {
            doGet(request, response);
        } else if (request.getMethod() == RequestMethod.POST) {
            doPost(request, response);
        } else if (request.getMethod() == RequestMethod.PUT) {
            doPut(request, response);
        } else if (request.getMethod() == RequestMethod.DELETE) {
            doDelete(request, response);
        }
    }

    public void doGet(Request request, Response response) throws ServletException, IOException {
    }

    public void doPost(Request request, Response response) throws ServletException, IOException {
    }

    public void doPut(Request request, Response response) throws ServletException, IOException {
    }

    public void doDelete(Request request, Response response) throws ServletException, IOException {
    }


}
