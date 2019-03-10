package cn.lws.server.servlet.user;

import cn.lws.server.exception.base.ServletException;
import cn.lws.server.response.Response;
import cn.lws.server.request.Request;
import cn.lws.server.servlet.base.HTTPServlet;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created by zl on 2019/03/01.
 */
@Slf4j
public class UserServlet extends HTTPServlet {
    @Override
    public void doGet(Request request, Response response) throws ServletException, IOException {
        if(request.getSession().getAttribute("username") != null){
            request.getRequestDispatcher("/views/user.html").forward(request,response);
        }else{
            //必须使用从浏览器角度看的路径，凡是静态资源，前面都没有/static or /views
            //但是forward是从服务器角度看的路径，是真实的、相对的路径
            response.sendRedirect("http://localhost:8080/login.html");
        }
    }
}
