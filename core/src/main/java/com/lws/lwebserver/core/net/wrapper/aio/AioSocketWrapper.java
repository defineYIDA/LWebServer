package com.lws.lwebserver.core.net.wrapper.aio;

import com.lws.lwebserver.core.net.endpoint.AbstractEndpoint;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @Author: zl
 * @Date: 2019/3/27 15:02
 */
public class AioSocketWrapper extends SocketWrapperBase<AsynchronousSocketChannel> {

    public AioSocketWrapper(AsynchronousSocketChannel socket, AbstractEndpoint endpoint) {
        super(socket, endpoint);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public AsynchronousSocketChannel getSocket() {
        return super.getSocket();
    }

    @Override
    public boolean isClosed() {
        return !socket.isOpen();
    }
}
