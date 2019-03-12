package com.lws.lwebserver.core.net.endpoint;

import org.springframework.util.StringUtils;

/**
 *
 * @author zl
 */
public abstract class AbstractEndpoint {

    /**
     * endpoint run status
     */
    protected volatile boolean running = false;
    /**
     * 阻塞状态
     */
    protected volatile boolean paused = false;

    /**
     * 启动服务器
     * @param port
     */
    public abstract void start(int port);

    /**
     * 关闭服务器
     */
    public abstract void close();

    /**
     * Hook to allow Endpoints to provide a specific Acceptor implementation.
     * 创建监听
     * @return acceptor
     */
    protected abstract Acceptor createAcceptor();

    /**
     * 根据传入的bio、nio、aio获取相应的Endpoint实例
     * @param connector
     * @return
     */
    public static AbstractEndpoint getInstance(String connector) {
        StringBuilder sb = new StringBuilder();
        sb.append("com.lws.lwebserver.core.net.endpoint")
                .append(".")
                .append(connector)
                .append(".")
                .append(StringUtils.capitalize(connector))
                .append("Endpoint");
        try {
            return (AbstractEndpoint) Class.forName(sb.toString()).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(connector);
    }

    /**
     *acceptor基类
     */
    public abstract static class Acceptor implements Runnable {
        //监听状态
        public enum AcceptorState {
            NEW, RUNNING, PAUSED, ENDED
        }

        protected volatile AcceptorState state = AcceptorState.NEW;
        public final AcceptorState getState() {
            return state;
        }

        private String threadName;
        protected final void setThreadName(final String threadName) {
            this.threadName = threadName;
        }
        protected final String getThreadName() {
            return threadName;
        }
    }


}
