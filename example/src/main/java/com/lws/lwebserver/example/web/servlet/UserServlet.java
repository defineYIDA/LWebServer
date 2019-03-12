package com.lws.lwebserver.example.web.servlet;

import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;
import com.lws.lwebserver.core.servlet.impl.HttpServlet;
import com.lws.lwebserver.sample.domain.User;
import com.lws.lwebserver.sample.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created by zl
 */
@Slf4j
public class UserServlet extends HttpServlet {
    private UserService userService;

    public UserServlet() {
        userService = UserService.getInstance();
    }
    
    @Override
    public void doGet(Request request, Response response) throws ServletException, IOException {
        User user = userService.findByUsername((String) request.getSession().getAttribute("username"));
        request.setAttribute("user",user);
        request.getRequestDispatcher("/views/user.html").forward(request, response);
    }
}
