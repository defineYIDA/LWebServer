package cn.lws.server.exception.handler;

import cn.lws.server.exception.RequestInvalidException;
import cn.lws.server.exception.base.ServletException;
import cn.lws.server.response.Response;
import cn.lws.server.util.IOUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

import static cn.lws.server.constant.Context.ERROR_PAGE;

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
