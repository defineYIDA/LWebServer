package com.lws.lwebserver.core.net.endpoint;

import com.lws.lwebserver.core.net.base.SocketProcessorBase;
import com.lws.lwebserver.core.net.dispatcher.AbstractDispatcher;
import com.lws.lwebserver.core.net.dispatcher.nio.NioDispatcher;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author zl
 */
@Slf4j
public abstract class AbstractEndpoint<S> {

    /**
     * endpoint run status
     */
    protected volatile boolean running = false;
    /**
     * 阻塞状态
     */
    protected volatile boolean paused = false;
    /**
     * 调度者
     */
    protected AbstractDispatcher dispatcher;
    /**
     * 接收者
     */
    protected Acceptor[] acceptors;
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
     * 创建调度者
     * @return
     */
    protected abstract void initDispatcher();
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
     * External Executor based thread pool.
     */
   /* private Executor executor = null;//内部线程池

    protected volatile boolean internalExecutor = true;//线程池是否被初始化

    public void setExecutor(Executor executor) {
        this.executor = executor;
        this.internalExecutor = (executor == null);
    }
    public Executor getExecutor() { return executor; }

    public void createExecutor(){
        internalExecutor=true;
        TaskQueue taskqueue = new TaskQueue();
        TaskThreadFactory tf = new TaskThreadFactory(getName() + "-exec-", daemon, getThreadPriority());
        executor = new ThreadPoolExecutor(getMinSpareThreads(), getMaxThreads(), 60, TimeUnit.SECONDS,taskqueue, tf);
        taskqueue.setParent( (ThreadPoolExecutor) executor);
    }*/

    /**
     * Acceptor thread count.
     */
    protected int acceptorThreadCount = 1;
    public void setAcceptorThreadCount(int acceptorThreadCount) {
        this.acceptorThreadCount = acceptorThreadCount;
    }
    public int getAcceptorThreadCount() { return acceptorThreadCount; }

    protected final void startAcceptorThreads() {
        int count = getAcceptorThreadCount();
        acceptors = new Acceptor[count];

        for (int i = 0; i < count; i++) {
            acceptors[i] = createAcceptor();
            String threadName =   "NIO-Acceptor-" + i;
            acceptors[i].setThreadName(threadName);
            Thread t = new Thread(acceptors[i], threadName);
            t.setPriority(Thread.NORM_PRIORITY);
            t.setDaemon(true);
            t.start();
        }
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

    /**
     *
     * @param socketWrapper
     * @return
     */
    public boolean processSocket(SocketWrapperBase socketWrapper){
        try {
            if(socketWrapper==null){
                return false;
            }
            //TODO SocketProcessorBase
            //SocketProcessorBase<S> sc=createSocketProcessor(socketWrapper);
            //调度
            dispatcher.doDispatch(socketWrapper);
        }catch (RejectedExecutionException ree){
            log.warn("endpoint.executor.fail",ree);
            return false;
        }catch (Throwable t){
            log.error("endpoint.process.fail",t);
            return false;
        }
        return true;
    }

    protected abstract SocketProcessorBase<S> createSocketProcessor(
            SocketWrapperBase<S> socketWrapper );

}
