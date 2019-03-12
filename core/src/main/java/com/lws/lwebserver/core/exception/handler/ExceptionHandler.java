package com.lws.lwebserver.core.exception.handler;

import com.lws.lwebserver.core.exception.RequestInvalidException;
import com.lws.lwebserver.core.exception.base.ServletException;
import com.lws.lwebserver.core.response.Response;
import lombok.extern.slf4j.Slf4j;
import com.lws.lwebserver.core.util.IOUtil;

import java.io.IOException;
import java.net.Socket;

import static com.lws.lwebserver.core.constant.Context.ERROR_PAGE;
import static com.lws.lwebserver.core.constant.Context.ERROR_PAGE;

/**
 * Created by zl on 2019/03/01.
 */
@Slf4j
public class ExceptionHandler {
    
    public void handle(ServletException e, Response response, Socket client) {
        try {
            if (e instanceof RequestInvalidException) {
                log.info("请求无法读取，丢弃");
            } else {
                log.info("抛出异常:{}", e.getClass().getName());
                e.printStackTrace();
                response
                        .header(e.getStatus())
                        .body(
                                IOUtil.getBytesFromFile(
                                        String.format(ERROR_PAGE, String.valueOf(e.getStatus().getCode())))
                        )
                        .write();//直接写入
                log.info("错误消息已写入输出流");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
