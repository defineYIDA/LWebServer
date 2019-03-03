package cn.lws.server;

import cn.lws.server.servlet.base.DispatcherServlet;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by zl on 2019/03/01.
 */
@Slf4j
public class HTTPServer {
    private static final int PORT = 8080;//本地端口
    private ServerSocket server;//mian socket
    
    private Listener listener; //监听线程，

    private DispatcherServlet dispatcherServlet;

    public HTTPServer() {
        try {
            server = new ServerSocket(PORT);
            listener = new Listener();
            listener.start();
            dispatcherServlet = new DispatcherServlet();
            log.info("服务器启动");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        listener.shutdown();
        dispatcherServlet.shutdown();
    }

    private class Listener extends Thread {
        @Override
        public void interrupt() {
            try {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                super.interrupt();
            }
        }

        public void shutdown() {
            Thread.currentThread().interrupt();
        }

        @Override
        public void run() {
            log.info("开始监听");
            while (!Thread.currentThread().isInterrupted()) {
                Socket client;
                try {
                    //TCP的短连接，请求处理完即关闭
                    client = server.accept();//调用阻塞
                    log.info("client:{}", client);
                    dispatcherServlet.doDispatch(client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        HTTPServer server = new HTTPServer();
        Scanner scanner = new Scanner(System.in);//监听输入，用来终止server
        String order = null;
        while (scanner.hasNext()) {
            order = scanner.next();
            if (order.equals("EXIT")) {
                server.close();
                System.exit(0);
            }
        }
    }
}
