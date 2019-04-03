package com.lws.lwebserver.core.net.wrapper;

import com.lws.lwebserver.core.net.endpoint.AbstractEndpoint;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author zl
 */
@Slf4j
@Data
public abstract class SocketWrapperBase<E> {

    protected final E socket;
    protected final AbstractEndpoint endpoint;
    /**
     * 当前socket状态，用来判断长连接是否应该被清除
     * 如果缓存在poller中的socket的isWorking==false,且超过了
     * 最长保活时间，则将被清理
     */
    public volatile boolean isWorking;

    public SocketWrapperBase(E socket, AbstractEndpoint endpoint) {
        this.socket = socket;
        this.endpoint = endpoint;
        isWorking=false;
    }


    public abstract void close() throws IOException;

    public E getSocket() {
        return socket;
    }
    public abstract boolean isClosed();

}

