package com.lws.lwebserver.core.net.endpoint.aio;

import com.lws.lwebserver.core.net.base.SocketProcessorBase;
import com.lws.lwebserver.core.net.endpoint.AbstractEndpoint;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.net.wrapper.aio.AioSocketWrapper;

/**
 * @Author: zl
 * @Date: 2019/3/27 15:00
 */
public class AioEndpoint extends AbstractEndpoint<AioSocketWrapper> {

    
    @Override
    public void start(int port) {

    }

    @Override
    public void close() {

    }

    @Override
    protected Acceptor createAcceptor() {
        return null;
    }

    @Override
    protected void initDispatcher() {

    }

    @Override
    protected SocketProcessorBase<AioSocketWrapper> createSocketProcessor(SocketWrapperBase<AioSocketWrapper> socketWrapper) {
        return null;
    }
}
