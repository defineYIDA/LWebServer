package com.lws.lwebserver.core.fliter;

import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:13
 */
public interface FilterChain {
    /**
     * 当前filter放行，由后续的filter继续进行过滤
     * @param request
     * @param response
     */
    void doFilter(Request request, Response response) ;
}
