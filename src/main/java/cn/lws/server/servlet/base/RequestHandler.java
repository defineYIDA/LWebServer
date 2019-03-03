package cn.lws.server.servlet.base;

import cn.lws.server.exception.ServerErrorException;
import cn.lws.server.exception.ServletNotFoundException;
import cn.lws.server.exception.base.ServletException;
import cn.lws.server.exception.handler.ExceptionHandler;
import cn.lws.server.response.Response;
import cn.lws.server.request.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by zl on 2019/03/01.
 * Servlet运行容器
 */
@Data
@AllArgsConstructor
@Slf4j
public class RequestHandler implements Runnable {
    private Socket client;
    private Request request;
    private Response response;
    private HTTPServlet servlet;
    private ExceptionHandler exceptionHandler;
    
    @Override
    public void run() {
        try {
            if (servlet == null) {
                throw new ServletNotFoundException();
            }
            //为了让request能找得到response，以设置cookie
            request.setRequestHandler(this);
            servlet.service(request, response);
            response.write();
        } catch (ServletException e) {
            exceptionHandler.handle(e, response, client);
        } catch (Exception e) {
           //其他未知异常
            exceptionHandler.handle(new ServerErrorException(), response, client);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
