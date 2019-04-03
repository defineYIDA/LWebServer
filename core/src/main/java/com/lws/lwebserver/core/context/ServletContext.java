package com.lws.lwebserver.core.context;

import com.lws.lwebserver.core.context.holder.FilterHolder;
import com.lws.lwebserver.core.context.holder.ServletHolder;
import com.lws.lwebserver.core.cookie.Cookie;
import com.lws.lwebserver.core.exception.ServletNotFoundException;
import com.lws.lwebserver.core.exception.FilterNotFoundException;
import com.lws.lwebserver.core.fliter.Filter;
import com.lws.lwebserver.core.listener.HttpSessionListener;
import com.lws.lwebserver.core.listener.ServletContextListener;
import com.lws.lwebserver.core.listener.ServletRequestListener;
import com.lws.lwebserver.core.listener.event.HttpSessionEvent;
import com.lws.lwebserver.core.listener.event.ServletContextEvent;
import com.lws.lwebserver.core.listener.event.ServletRequestEvent;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;
import com.lws.lwebserver.core.servlet.Servlet;
import com.lws.lwebserver.core.session.HttpSession;
import com.lws.lwebserver.core.session.IdleSessionCleaner;
import com.lws.lwebserver.core.util.UUIDUtil;
import com.lws.lwebserver.core.util.XMLUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.util.AntPathMatcher;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.lws.lwebserver.core.constant.Const.DEFAULT_SERVLET_ALIAS;
import static com.lws.lwebserver.core.constant.Const.DEFAULT_SESSION_EXPIRE_TIME;

/**
 * @Author: zl
 * @Date: 2019/3/16 11:48
 */
@Data
@Slf4j
public class ServletContext {
    /**
     * 别名->类名
     * 一个Servlet类只能有一个Servlet别名，一个Servlet别名只能对应一个Servlet类
     */
    private Map<String, ServletHolder> servlets;
    /**
     * 一个Servlet可以对应多个URL，一个URL只能对应一个Servlet
     * URL Pattern -> Servlet别名
     */
    private Map<String, String> servletMapping;


    /**
     * 别名->类名
     */
    private Map<String, FilterHolder> filters;
    /**
     * URL Pattern -> 别名列表，注意同一个URLPattern可以对应多个Filter，但只能对应一个Servlet
     */
    private Map<String, List<String>> filterMapping;

    /**
     * 监听器们
     */
    private List<ServletContextListener> servletContextListeners;
    private List<HttpSessionListener> httpSessionListeners;
    private List<ServletRequestListener> servletRequestListeners;

    /**
     * 域
     */
    private Map<String, Object> attributes;
    /**
     * 整个应用对应的session们
     */
    private Map<String, HttpSession> sessions;
    /**
     * 路径匹配器，由Spring提供
     */
    private AntPathMatcher matcher;//TODO

    private IdleSessionCleaner idleSessionCleaner;//定时清理Session


    public ServletContext() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        init();
    }

    /**
     * 由URL得到对应的一个Servlet实例
     *
     * @param url
     * @return
     * @throws ServletNotFoundException
     */
    public Servlet mapServlet(String url) throws ServletNotFoundException {
        // 1、精确匹配
        String servletAlias = servletMapping.get(url);
        if (servletAlias != null) {
            return initAndGetServlet(servletAlias);
        }
        // 2、路径匹配
        List<String> matchingPatterns = new ArrayList<>();
        Set<String> patterns = servletMapping.keySet();
        for (String pattern : patterns) {
            if (matcher.match(pattern, url)) {
                matchingPatterns.add(pattern);
            }
        }

        if (!matchingPatterns.isEmpty()) {
            Comparator<String> patternComparator = matcher.getPatternComparator(url);
            Collections.sort(matchingPatterns, patternComparator);
            String bestMatch = matchingPatterns.get(0);
            return initAndGetServlet(bestMatch);
        }
        return initAndGetServlet(DEFAULT_SERVLET_ALIAS);
    }

    /**
     * 初始化并获取Servlet实例，如果已经初始化过则直接返回
     *
     * @param servletAlias
     * @return
     * @throws ServletNotFoundException
     */
    private Servlet initAndGetServlet(String servletAlias) throws ServletNotFoundException {
        ServletHolder servletHolder = servlets.get(servletAlias);
        if (servletHolder == null) {
            throw new ServletNotFoundException();
        }
        if (servletHolder.getServlet() == null) {
            try {
                Servlet servlet = (Servlet) Class.forName(servletHolder.getServletClass()).newInstance();
                servlet.init();
                servletHolder.setServlet(servlet);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return servletHolder.getServlet();
    }


    /**
     * 由URL得到一系列匹配的Filter实例
     *
     * @param url
     * @return
     */
    public List<Filter> mapFilter(String url) throws FilterNotFoundException {
        List<String> matchingPatterns = new ArrayList<>();
        Set<String> patterns = filterMapping.keySet();
        for (String pattern : patterns) {
            if (matcher.match(pattern, url)) {
                matchingPatterns.add(pattern);
            }
        }
        //flatMap将一对多转化为一对一
        Set<String> filterAliases = matchingPatterns.stream().flatMap(pattern -> this.filterMapping.get(pattern).stream()).collect(Collectors.toSet());
        List<Filter> result = new ArrayList<>();
        for (String alias : filterAliases) {
            result.add(initAndGetFilter(alias));
        }
        return result;
    }

    /**
     * 初始化并返回Filter实例，如果已经初始化过则直接返回
     *
     * @param filterAlias
     * @return
     * @throws FilterNotFoundException
     */
    private Filter initAndGetFilter(String filterAlias) throws FilterNotFoundException {
        FilterHolder filterHolder = filters.get(filterAlias);
        if (filterHolder == null) {
            throw new FilterNotFoundException();
        }
        if (filterHolder.getFilter() == null) {
            try {
                Filter filter = (Filter) Class.forName(filterHolder.getFilterClass()).newInstance();
                filter.init();
                filterHolder.setFilter(filter);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return filterHolder.getFilter();
    }

    /**
     * 应用初始化
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public void init() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        this.servlets = new HashMap<>();
        this.servletMapping = new HashMap<>();
        this.attributes = new ConcurrentHashMap<>();
        this.sessions = new ConcurrentHashMap<>();
        this.filters = new HashMap<>();
        this.filterMapping = new HashMap<>();
        this.matcher = new AntPathMatcher();
        //TODO 清理session
        //this.idleSessionCleaner = new IdleSessionCleaner();
        //this.idleSessionCleaner.start();

        this.servletContextListeners = new ArrayList<>();
        this.httpSessionListeners = new ArrayList<>();
        this.servletRequestListeners = new ArrayList<>();

        parseConfig();//解析web.xml
        //Listener
        ServletContextEvent servletContextEvent = new ServletContextEvent(this);
        for (ServletContextListener listener : servletContextListeners) {
            listener.contextInitialized(servletContextEvent);
        }
    }

    /**
     * 应用关闭前被调用
     */
    public void destroy() {
        servlets.values().forEach(servletHolder -> {
            if (servletHolder.getServlet() != null) {
                servletHolder.getServlet().destroy();
            }
        });
        filters.values().forEach(filterHolder -> {
            if (filterHolder.getFilter() != null) {
                filterHolder.getFilter().destroy();
            }
        });
        //Listener
        ServletContextEvent servletContextEvent = new ServletContextEvent(this);
        for (ServletContextListener listener : servletContextListeners) {
            listener.contextDestroyed(servletContextEvent);
        }
    }

    /**
     * web.xml文件解析，比如servlet，filter，listener等
     *
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void parseConfig() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Document doc = XMLUtil.getDocument(ServletContext.class.getResourceAsStream("/web.xml"));
        Element root = doc.getRootElement();
        // 解析servlet
        List<Element> servlets = root.elements("servlet");
        for (Element servletEle : servlets) {
            String key = servletEle.element("servlet-name").getText();
            String value = servletEle.element("servlet-class").getText();
            this.servlets.put(key, new ServletHolder(value));
        }
        //解析Servlet-mapping'
        List<Element> servletMapping = root.elements("servlet-mapping");
        for (Element mapping : servletMapping) {
            List<Element> urlPatterns = mapping.elements("url-pattern");//一个servlet可以对应多个url
            String value = mapping.element("servlet-name").getText();
            for (Element urlPattern : urlPatterns) {
                this.servletMapping.put(urlPattern.getText(), value);
            }
        }

        // 解析 filter
        List<Element> filters = root.elements("filter");
        for (Element filterEle : filters) {
            String key = filterEle.element("filter-name").getText();
            String value = filterEle.element("filter-class").getText();
            this.filters.put(key, new FilterHolder(value));
        }
        //解析 filter-mapping
        List<Element> filterMapping = root.elements("filter-mapping");
        for (Element mapping : filterMapping) {
            List<Element> urlPatterns = mapping.elements("url-pattern");//一个filter可以对应多个url，一个url也可以对应不同的filter
            String value = mapping.element("filter-name").getText();
            for (Element urlPattern : urlPatterns) {
                /**
                 * 判断该url是否已经存在过，如果存在，即一个url对应多个filter的情况，
                 * 例如：/**
                 */
                List<String> values = this.filterMapping.get(urlPattern.getText());
                if (values == null) {
                    values = new ArrayList<>();
                }
                values.add(value);
                this.filterMapping.put(urlPattern.getText(), values);
            }
        }

        // 解析listener
        Element listener = root.element("listener");
        List<Element> listenerEles = listener.elements("listener-class");
        for (Element listenerEle : listenerEles) {
            EventListener eventListener = (EventListener) Class.forName(listenerEle.getText()).newInstance();
            if (eventListener instanceof ServletContextListener) {
                servletContextListeners.add((ServletContextListener) eventListener);
            }
            if (eventListener instanceof HttpSessionListener) {
                httpSessionListeners.add((HttpSessionListener) eventListener);
            }
            if (eventListener instanceof ServletRequestListener) {
                servletRequestListeners.add((ServletRequestListener) eventListener);
            }
        }
    }

    /**
     * 获取session
     * @param JSESSIONID
     * @return
     */
    public HttpSession getSession(String JSESSIONID) {
        return sessions.get(JSESSIONID);
    }

    /**
     * 创建session
     * @param response
     * @return
     */
    public HttpSession createSession(Response response) {
        HttpSession session = new HttpSession(UUIDUtil.uuid());
        sessions.put(session.getId(), session);
        response.addCookie(new Cookie("JSESSIONID", session.getId()));

        HttpSessionEvent httpSessionEvent = new HttpSessionEvent(session);
        for (HttpSessionListener listener : httpSessionListeners) {
            listener.sessionCreated(httpSessionEvent);
        }
        return session;
    }

    /**
     * 销毁session
     * @param session
     */
    public void invalidateSession(HttpSession session) {
        sessions.remove(session.getId());
        afterSessionDestroyed(session);
    }

    /**
     * 清除空闲的session
     * 由于ConcurrentHashMap是线程安全的，所以remove不需要进行加锁
     */
    public void cleanIdleSessions() {
        for (Iterator<Map.Entry<String, HttpSession>> it = sessions.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, HttpSession> entry = it.next();
            if (Duration.between(entry.getValue().getLastAccessed(), Instant.now()).getSeconds() >= DEFAULT_SESSION_EXPIRE_TIME) {
//                log.info("该session {} 已过期", entry.getKey());
                afterSessionDestroyed(entry.getValue());
                it.remove();
            }
        }
    }

    private void afterSessionDestroyed(HttpSession session) {
        HttpSessionEvent httpSessionEvent = new HttpSessionEvent(session);
        for (HttpSessionListener listener : httpSessionListeners) {
            listener.sessionDestroyed(httpSessionEvent);
        }
    }

    public void afterRequestCreated(Request request) {
        ServletRequestEvent servletRequestEvent = new ServletRequestEvent(this, request);
        for (ServletRequestListener listener : servletRequestListeners) {
            listener.requestInitialized(servletRequestEvent);
        }
    }

    public void afterRequestDestroyed(Request request) {
        ServletRequestEvent servletRequestEvent = new ServletRequestEvent(this, request);
        for (ServletRequestListener listener : servletRequestListeners) {
            listener.requestDestroyed(servletRequestEvent);
        }
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
}
