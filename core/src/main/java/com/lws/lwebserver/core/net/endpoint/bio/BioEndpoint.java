package com.lws.lwebserver.core.net.endpoint.bio;

import com.lws.lwebserver.core.net.base.SocketProcessorBase;
import com.lws.lwebserver.core.net.dispatcher.bio.BioDispatcher;
import com.lws.lwebserver.core.net.endpoint.AbstractEndpoint;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.net.wrapper.bio.BioSocketWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
@Slf4j
public class BioEndpoint extends AbstractEndpoint<BioSocketWrapper> {
    private ServerSocket server;//主socket
    private volatile boolean isRunning = true;

    @Override
    public void start(int port) {
        try {
            initDispatcher();//初始化调度者
            server = new ServerSocket(port);
            startAcceptorThreads();//开始监听
            log.info("服务器启动");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("初始化服务器失败");
            close();
        }
    }


    @Override
    public void close() {
        isRunning = false;
        dispatcher.shutdown();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket accept() throws IOException {
        return server.accept();
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    protected void initDispatcher() {
        dispatcher = new BioDispatcher();
    }

    @Override
    public boolean processSocket(SocketWrapperBase socketWrapper) {
        return super.processSocket(socketWrapper);
    }

    @Override
    protected SocketProcessorBase<BioSocketWrapper> createSocketProcessor(SocketWrapperBase<BioSocketWrapper> socketWrapper) {
        return null;
    }
    //-----------------------------------------------------acceptor start

    @Override
    protected Acceptor createAcceptor() {
        return new Acceptor();
    }
    /**
     * 后台线程监听客户机的TCP连接
     */
    protected class Acceptor extends AbstractEndpoint.Acceptor{
        @Override
        public void run() {
            log.info("BIO Acceptor 开始监听");
            while (running) {
                //endpoint阻塞
                while (paused && running) {
                    state = AcceptorState.PAUSED;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
                if(!running){
                    break;
                }
                state=AcceptorState.RUNNING;
                Socket client=null;
                try {
                    //TCP的短连接，请求处理完即关闭
                    client = server.accept();
                    log.info("client:{}", client);

                    //TODO 调度
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //-----------------------------------------------------acceptor end
}
