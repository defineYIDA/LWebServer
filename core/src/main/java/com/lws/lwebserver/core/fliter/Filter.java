package com.lws.lwebserver.core.fliter;

import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:13
 */
public interface Filter {
    /**
     * 过滤器初始化
     */
    void init();

    /**
     * 过滤
     * @param request
     * @param response
     * @param filterChain
     */
    void doFilter(Request request, Response response, FilterChain filterChain) ;

    /**
     * 过滤器销毁
     */
    void destroy();
}
