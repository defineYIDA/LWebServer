package com.lws.lwebserver.example.web.servlet;

import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;
import com.lws.lwebserver.core.servlet.impl.HttpServlet;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created by zl
 */
@Slf4j
public class LogoutServlet extends HttpServlet {
    
    @Override
    public void doGet(Request request, Response response) throws ServletException, IOException {
        request.getRequestDispatcher("/views/logout.html").forward(request,response);  
    }

    @Override
    public void doPost(Request request, Response response) throws ServletException, IOException {
        request.getSession().removeAttribute("username");
        request.getSession().invalidate();
        response.sendRedirect("/login");
    }
}
