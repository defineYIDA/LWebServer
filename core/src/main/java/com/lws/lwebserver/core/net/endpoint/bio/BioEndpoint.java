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
    private ServerSocket serverSocket;//主socket

    @Override
    public void start(int port) {
        try {
            running=true;
            paused=false;
            initDispatcher();//初始化调度者
            serverSocket = new ServerSocket(port);
            startAcceptorThreads("BIO-Acceptor");//开始监听
            log.info("服务器启动");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("初始化服务器失败");
            close();
        }
    }


    @Override
    public void close() {
        running = false;
        dispatcher.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initDispatcher() {
        dispatcher = new BioDispatcher();
    }

    @Override
    protected SocketProcessorBase<BioSocketWrapper> createSocketProcessor(SocketWrapperBase<BioSocketWrapper> socketWrapper) {
        //TODO
        return null;
    }
    /**
     * 包装socket，并处理
     * @param socket
     * @return
     */
    protected boolean setSocketOptions(Socket socket) {
        try {
            return processSocket(new BioSocketWrapper(socket,this));
        }catch (Exception e){
            log.error("处理clientSocket错误："+e);
            return false;
        }
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
                Socket clientSocket=null;
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
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /*try {
                    //TCP的短连接，请求处理完即关闭
                    client = serverSocket.accept();
                    log.info("client:{}", client);
                    processSocket(new BioSocketWrapper(client,));
                    //TODO 调度
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }
    //-----------------------------------------------------acceptor end
}
