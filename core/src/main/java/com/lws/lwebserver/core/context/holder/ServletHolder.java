package com.lws.lwebserver.core.context.holder;

import com.lws.lwebserver.core.servlet.Servlet;
import lombok.Data;

/**
 * @Author: zl
 * @Date: 2019/3/16 11:51
 */
@Data
public class ServletHolder {
    private Servlet servlet;
    private String servletClass;

    public ServletHolder(String servletClass) {
        this.servletClass = servletClass;
    }
}
