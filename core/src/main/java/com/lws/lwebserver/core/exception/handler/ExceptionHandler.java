package com.lws.lwebserver.core.exception.handler;

import com.lws.lwebserver.core.exception.RequestInvalidException;
import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;
import com.lws.lwebserver.core.response.Header;
import com.lws.lwebserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;
import com.lws.lwebserver.core.util.IOUtil;

import java.io.IOException;
import java.net.Socket;

import static com.lws.lwebserver.core.constant.Const.ERROR_PAGE;

/**
 * Created by zl on 2019/03/01.
 */
@Slf4j
public class ExceptionHandler {

    public void handle(ServletException e, Response response, SocketWrapperBase socketWrapper) {
        try {
            if (e instanceof RequestInvalidException) {
                //log.info("请求无法读取，丢弃");
                socketWrapper.close();
            } else {
                log.info("抛出异常:{}", e.getClass().getName());
                e.printStackTrace();
                response.addHeader(new Header("Connection", "close"));
                response.setStatus(e.getStatus());
                response.setBody(IOUtil.getBytesFromFile(
                        String.format(ERROR_PAGE, String.valueOf(e.getStatus().getCode()))));
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
