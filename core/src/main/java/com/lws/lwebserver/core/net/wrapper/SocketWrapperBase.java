package com.lws.lwebserver.core.net.wrapper;

import com.lws.lwebserver.core.net.endpoint.AbstractEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author zl
 */
@Slf4j
public abstract class SocketWrapperBase<E> {

    protected final E socket;
    protected final AbstractEndpoint endpoint;

    public SocketWrapperBase(E socket, AbstractEndpoint endpoint) {
        this.socket = socket;
        this.endpoint = endpoint;
    }


    public abstract void close() throws IOException;
}
