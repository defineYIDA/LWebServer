package com.lws.lwebserver.core.net.wrapper.nio;

import com.lws.lwebserver.core.net.endpoint.AbstractEndpoint;
import com.lws.lwebserver.core.net.endpoint.nio.Poller;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;

import java.io.IOException;
import java.nio.channels.SocketChannel;


/**
 * @Author: zl
 * @Date: 2019/3/12 21:18
 */
public class NioSocketWrapper extends SocketWrapperBase<SocketChannel> {



    private Poller poller = null;




    public NioSocketWrapper(SocketChannel socket, AbstractEndpoint endpoint,Poller poller) {
        super(socket, endpoint);
        this.poller=poller;
    }
    public SocketChannel getSocketChannel(){
        return this.socket;
    }
    public Poller getPoller() {
        return poller;
    }
    @Override
    public void close() throws IOException {
        socket.keyFor(poller.getSelector()).cancel();//关闭客户通道
        socket.close();
    }

}
