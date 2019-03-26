package com.lws.lwebserver.core.net.wrapper.bio;

import com.lws.lwebserver.core.net.endpoint.AbstractEndpoint;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;

import java.io.IOException;
import java.net.Socket;

/**
 * @Author: zl
 * @Date: 2019/3/19 23:38
 */
public class BioSocketWrapper extends SocketWrapperBase<Socket> {

    public BioSocketWrapper(Socket socket, AbstractEndpoint endpoint) {
        super(socket, endpoint);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public Socket getSocket() {
        return super.getSocket();
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }
}
