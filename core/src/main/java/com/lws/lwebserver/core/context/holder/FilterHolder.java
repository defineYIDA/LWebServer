package com.lws.lwebserver.core.context.holder;

import com.lws.lwebserver.core.fliter.Filter;
import lombok.Data;

/**
 * @Author: zl
 * @Date: 2019/3/16 11:50
 */
@Data
public class FilterHolder {
    private Filter filter;
    private String filterClass;

    public FilterHolder(String filterClass) {
        this.filterClass = filterClass;
    }
}
