package com.lws.lwebserver.core;

import com.lws.lwebserver.core.net.endpoint.AbstractEndpoint;
import com.lws.lwebserver.core.util.PropertyUtil;

import java.util.Scanner;

/**
 * @author ZL
 */
public class BootStrap {
    /**
     * 服务器入口(启动引导)
     */
    public static void run() {
        String port = PropertyUtil.getProperty("server.port");//获得服务器端口
        if (null == port) {
            throw new IllegalArgumentException("server.port 不存在");
        }
        //获得当前设置的   IO模式
        String connector = PropertyUtil.getProperty("server.connector");
        if (connector == null ||
                (!connector.equalsIgnoreCase("bio")
                && !connector.equalsIgnoreCase("nio")
                && !connector.equalsIgnoreCase("aio"))
        ) {
            throw new IllegalArgumentException("server.network 不存在或不符合规范");
        }
        //获得具体io模式对应的endpoint实例
        AbstractEndpoint server = AbstractEndpoint.getInstance(connector);
        server.start(Integer.parseInt(port));
        Scanner scanner = new Scanner(System.in);
        String order;
        while (scanner.hasNext()) {
            order = scanner.next();
            if (order.equals("EXIT")) {
                server.close();
                System.exit(0);
            }
        }

    }
}
