package com.lws.lwebserver.core.net.endpoint.nio;

import com.lws.lwebserver.core.net.endpoint.AbstractEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author zl
 * nio 模式切入点
 */
@Slf4j
public class NioEndpoint extends AbstractEndpoint {

    private ServerSocketChannel serverSocket;//服务通道


    @Override
    public void start(int port) {
        running=true;//run endpoint
    }

    @Override
    public void close() {

    }

    @Override
    protected Acceptor createAcceptor() {
        return new Acceptor();
    }

    /**
     *处理获得客户机连接
     * 设置阻塞类型；
     * 注册到poller
     * @param socket
     * @return
     */
    protected boolean setSocketOptions(SocketChannel socket) {
        try {
            //注意监听serversocketchannel为阻塞，客户机连接为非阻塞
            socket.configureBlocking(false);
            //TODO SecureNioChannel SSL 对buffer进行加密

        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 关闭通道
     * @param socket
     */
    private void closeSocket(SocketChannel socket) {
        try {
            socket.socket().close();
        } catch (IOException ioe)  {
            if (log.isDebugEnabled()) {
                log.error("endpoint.err.close");
            }
        }
        try {
            socket.close();
        } catch (IOException ioe) {
            if (log.isDebugEnabled()) {
                log.error("endpoint.err.close");
            }
        }
    }
    //-----------------------------------------------------acceptor start
    /**
     * 后台线程监听客户机的TCP连接
     */
    protected class Acceptor extends AbstractEndpoint.Acceptor{
        @Override
        public void run() {
            while (running){
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
                //TODO 待添加最大连接数的判断 LimitLatch
                SocketChannel  clientSocket =null;
                try {
                    //调用阻塞
                    clientSocket=serverSocket.accept();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(null==clientSocket){
                    continue;
                }
                if(running&&!paused){
                    if(!setSocketOptions(clientSocket)){
                        closeSocket(clientSocket);
                    }
                }else {
                   closeSocket(clientSocket);
                }

            }
        }
    }
    //-----------------------------------------------------acceptor end



}
