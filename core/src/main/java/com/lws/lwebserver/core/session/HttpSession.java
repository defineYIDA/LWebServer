package com.lws.lwebserver.core.session;

import com.lws.lwebserver.core.context.WebApplication;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zl
 * @Date: 2019/3/16 16:27
 */
public class HttpSession {
    private String id;
    private Map<String, Object> attributes;
    private boolean isValid;
    /**
     * 用于判断sessiion是否过期，标准为当前时间-上次访问时间 >= 阈值
     */
    private Instant lastAccessed;


    public HttpSession(String id) {
        this.id = id;
        this.attributes = new ConcurrentHashMap<>();
        this.isValid = true;
        this.lastAccessed = Instant.now();
    }

    /**
     * 使当前session失效，之后就无法读写当前session了
     * 并会清除session数据，并且在servletContext中删除此session
     */
    public void invalidate() {
        this.isValid = false;
        this.attributes.clear();
        WebApplication.getServletContext().invalidateSession(this);
    }

    public Object getAttribute(String key) {
        if (isValid) {
            this.lastAccessed = Instant.now();
            return attributes.get(key);
        }
        throw new IllegalStateException("session has invalidated");
    }

    public void setAttribute(String key, Object value) {
        if (isValid) {
            this.lastAccessed = Instant.now();
            attributes.put(key, value);
        } else {
            throw new IllegalStateException("session has invalidated");
        }
    }

    public String getId() {
        return id;
    }

    public Instant getLastAccessed() {
        return lastAccessed;
    }


    public void removeAttribute(String key) {
        attributes.remove(key);
    }


}

