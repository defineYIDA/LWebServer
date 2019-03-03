package cn.lws.server.resource;

import cn.lws.server.enumeration.HTTPStatus;
import cn.lws.server.exception.RequestParseException;
import cn.lws.server.exception.ResourceNotFoundException;
import cn.lws.server.exception.base.ServletException;
import cn.lws.server.exception.handler.ExceptionHandler;
import cn.lws.server.response.Response;
import cn.lws.server.util.IOUtil;
import cn.lws.server.util.MimeTypeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by zl on 2019/03/01.
 * 处理静态资源
 */
@Slf4j
public class ResourceHandler {
    private ExceptionHandler exceptionHandler;

    public ResourceHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
    
    public void handle(String url, Response response, Socket client) {
        try {
            if (ResourceHandler.class.getResource(url) == null) {
                log.info("找不到该资源:{}",url);
                throw new ResourceNotFoundException();
            }
            response.header(HTTPStatus.OK, MimeTypeUtil.getTypes(url)).body(IOUtil.getBytesFromFile(url)).write();
            log.info("{}已写入输出流", url);
        } catch (IOException e) {
            e.printStackTrace();
            exceptionHandler.handle(new RequestParseException(), response, client);
        } catch (ServletException e) {
            exceptionHandler.handle(e, response, client);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}