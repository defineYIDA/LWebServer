package com.lws.lwebserver.core.net.dispatcher.nio;


import com.lws.lwebserver.core.exception.ServerErrorException;
import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.net.dispatcher.AbstractDispatcher;
import com.lws.lwebserver.core.net.handler.nio.NioRequestHandler;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.net.wrapper.nio.NioSocketWrapper;
import com.lws.lwebserver.core.request.Request;
import com.lws.lwebserver.core.response.Response;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Author: zl
 * @Date: 2019/3/16 10:25
 */
@Data
@Slf4j
public class NioDispatcher extends AbstractDispatcher {
    /**
     * 分发请求，注意IO读取必须放在IO线程中进行，不能放到线程池中，否则会出现多个线程同时读同一个socket数据的情况
     * 1、读取数据
     * 2、构造request，response
     * 3、将业务放入到线程池中处理
     * @param socketWrapper
     */
    @Override
    public void doDispatch(SocketWrapperBase socketWrapper) {
        NioSocketWrapper nioSocketWrapper = (NioSocketWrapper) socketWrapper;
        log.info("已经将请求放入worker线程池中");
        //定义缓存
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        log.info("开始读取Request");
        Request request = null;
        Response response = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //将通道中的数据读到缓存中，通道中的数据就是clint发送给服务端的数据
            //int readLength=nioSocketWrapper.getSocketChannel().read(buffer);
            while (nioSocketWrapper.getSocket().read(buffer)> 0) {
                /**
                 * NIO中最复杂的操作就是buffer的控制
                 * buffer中有一个游标，游标信息在操作后不会归零，如果直接访问buffer会有数据不一致的可能
                 * filp重置游标，nio中filp是常用方法
                 *TODO buffer应用(写)的固定逻辑：
                 *  1，clear();
                 *  2， put() ->写操作
                 *  3，flip() ->重置游标
                 *  4，scocketchannel.write(buffer) ->将缓存数据发送到网络的另一端
                 *  5，clear()
                 *  TODO 读
                 *  1，clear();
                 *  2，scocketchannel.read(buffer) ->从网络中读取数据
                 *  3，flip() ->重置游标
                 *  4，clear()
                 */
                buffer.flip();
                //buffer.remaining获得字节长度
                //将bytebuffer保存到字节数组中，
                baos.write(buffer.array());
            }
            baos.close();//关闭通道
            request = new Request(baos.toByteArray());//实例request对象
            response = new Response();//实例response对象，区别与1.0
            pool.execute(new NioRequestHandler(nioSocketWrapper, servletContext, exceptionHandler, resourceHandler, request, response));
        } catch (IOException e) {
            e.printStackTrace();
            exceptionHandler.handle(new ServerErrorException(), response, nioSocketWrapper);
        } catch (ServletException e) {
            exceptionHandler.handle(e, response, nioSocketWrapper);
        }
    }
}
