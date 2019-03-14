package com.lws.lwebserver.core.net.wrapper.nio;

import com.lws.lwebserver.core.net.endpoint.AbstractEndpoint;
import com.lws.lwebserver.core.net.endpoint.nio.NioEndpoint;
import com.lws.lwebserver.core.net.endpoint.nio.Poller;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import lombok.Data;

import java.io.IOException;
import java.nio.channels.SocketChannel;


/**
 * @Author: zl
 * @Date: 2019/3/12 21:18
 */
@Data
public class NioSocketWrapper extends SocketWrapperBase<SocketChannel> {



    private Poller poller = null;
    private volatile long waitBegin;//在建立连接/keep-alive时设置waitBegin
    private volatile boolean isWorking;





    public NioSocketWrapper(SocketChannel socket, NioEndpoint endpoint, Poller poller) {
        super(socket, endpoint);
        this.poller=poller;
        isWorking=false;
    }
    public SocketChannel getSocketChannel(){
        return this.socket;
    }

    @Override
    public void close() throws IOException {
        socket.keyFor(poller.getSelector()).cancel();//关闭客户通道
        socket.close();
    }

}
