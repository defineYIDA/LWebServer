package cn.lws.server.response;

import cn.lws.server.constant.CharsetProperties;
import cn.lws.server.enumeration.HTTPStatus;
import cn.lws.server.model.Cookie;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static cn.lws.server.constant.CharConstant.BLANK;
import static cn.lws.server.constant.CharConstant.CRLF;
import static cn.lws.server.constant.CharsetProperties.UTF_8_CHARSET;
import static cn.lws.server.constant.Context.DEFAULT_CONTENT_TYPE;

/**
 * Created by zl on 2019/03/01.
 * HTTP/1.1 200 OK
 * Date: Sat, 31 Dec 2005 23:59:59 GMT
 * Content-Type: text/html;constant=ISO-8859-1
 * Content-Length: 122
 * <p>
 * ＜html＞
 * ＜head＞
 * ＜title＞Wrox Homepage＜/title＞
 * ＜/head＞
 * ＜body＞
 * ＜!-- body goes here --＞
 * ＜/body＞
 * ＜/html＞
 */
@Slf4j
public class Response {
    private StringBuilder headerAppender;
    private StringBuilder bodyAppender;
    private List<Cookie> cookies;
    private List<Header> headers;
    private byte[] body;
    private OutputStream os;
    
    public Response(OutputStream os) {
        this.os = os;//传入socket的outputstream
        this.headerAppender = new StringBuilder();
        this.bodyAppender = new StringBuilder();
        this.cookies = new ArrayList<>();
        this.headers = new ArrayList<>();
    }
    
    public void addCookie(Cookie cookie){
        cookies.add(cookie);
    }
    
    public void addHeader(Header header){
        headers.add(header);
    }
    
    public Response header(HTTPStatus status) {
        return header(status, DEFAULT_CONTENT_TYPE);
    }
    
 
    public Response header(HTTPStatus status, String contentType) {
        if (contentType == null) {
            contentType = DEFAULT_CONTENT_TYPE;
        }
        //HTTP/1.1 200 OK
        headerAppender.append("HTTP/1.1").append(BLANK).append(status.getCode()).append(BLANK).append(status).append(CRLF);
        //Date: Sat, 31 Dec 2005 23:59:59 GMT
        headerAppender.append("Date:").append(BLANK).append(new Date()).append(CRLF);
        headerAppender.append("Content-Type:").append(BLANK).append(contentType).append(CRLF);
        if(headers != null) {
            for (Header header : headers) {
                headerAppender.append(header.getKey()).append(":").append(BLANK).append(header.getValue()).append(CRLF);
            }
        }
        if(cookies.size() > 0){
            for(Cookie cookie:cookies){
                headerAppender.append("Set-Cookie:").append(BLANK).append(cookie.getKey()).append("=").append(cookie.getValue()).append(CRLF);
            }
        }
        headerAppender.append("Content-Length:").append(BLANK);
        return this;
    }
    
    
    
    //一次性传入响应体
    public Response body(byte[] body) {
        this.headerAppender.append(body.length).append(CRLF).append(CRLF);
        this.body = body;
        return this;
    }
    
    public Response print(String content) {
        bodyAppender.append(content);
        return this;
    }
    
    public Response println(String content) {
        bodyAppender.append(content);
        bodyAppender.append(CRLF);
        return this;
    }
    
    
    public void write() {
        //默认返回OK
        if(this.headerAppender.toString().length() == 0){
            header(HTTPStatus.OK);
        }
        
        //如果是多次使用print或println构建的响应体，而非一次性传入
        if(body == null){
            log.info("多次使用print或println构建的响应体");
            body(bodyAppender.toString().getBytes(CharsetProperties.UTF_8_CHARSET));
        }
        
        byte[] header = this.headerAppender.toString().getBytes(UTF_8_CHARSET);
        
        //生成响应报文
        byte[] response = new byte[header.length + body.length];
        System.arraycopy(header, 0, response, 0, header.length);
        System.arraycopy(body, 0, response, header.length, body.length);
        try {
            os.write(response);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void sendRedirect(String url){
        log.info("重定向至{}",url);
        addHeader(new Header("Location",url));
        header(HTTPStatus.MOVED_TEMPORARILY);
        body(bodyAppender.toString().getBytes(CharsetProperties.UTF_8_CHARSET));
    }
}
