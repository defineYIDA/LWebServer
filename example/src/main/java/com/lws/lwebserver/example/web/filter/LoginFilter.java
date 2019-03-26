package com.lws.lwebserver.example.web.filter;



import com.lws.lwebserver.core.fliter.Filter;
import com.lws.lwebserver.core.fliter.FilterChain;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zl
 */
@Slf4j
public class LoginFilter implements Filter {

    @Override
    public void init() {
        log.info("LoginFilter init...");
    }

    @Override
    public void doFilter(Request request, Response response, FilterChain filterChain) {
        log.info("当前访问的servletPath:{}", request.getServletPath());
        // login直接放行，其他页面访问均需要登录
        if (request.getServletPath().equals("/login") || request.getServletPath().startsWith("/views/errors")) {
            log.info("直接放行");
            filterChain.doFilter(request, response);
        } else {
            log.info("检查是否登录...");
            if (request.getSession(false) != null && request.getSession().getAttribute("username") != null) {
                log.info("已登录，通过检查...");
                filterChain.doFilter(request, response);
            } else {
                log.info("未登录,401");
                // 未登录。重定向至登录页面
                response.sendRedirect("/login");
            }
        }
    }

    @Override
    public void destroy() {
        log.info("LoginFilter destroy...");
    }
}
