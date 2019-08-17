# LWebServer
`简化版Tomcat` ，可以配置BIO,NIO,AIO三种模式，实现Cookie和Session的会话追踪，请求响应的封装，Servlet的调度，长连接等Web服务器基本功能。

基于Java BIO/NIO/AIO(NIO2)包、多线程、Socket网络编程、XML解析、log4j/slf4j日志

NIO的实现参照的是Tomcat的源码

## 学习内容：
`IO模式` / `Socket` / `TCP/IP` / `框架源码(Tomcat,Spring,Netty等)` / `高级网络编程` / `线程模型`

## 实现功能：
- BIO NIO AIO `NIO使用的是IO多路复用的Select机制`

- Reactor 模式

- HTTP Protocol `Keep-Alive`

- Servlet `ServletContext` `Dispatcher`
  
- Request&Response

- Forward&Redirect

- session&cookie



## 解决问题

- AIO的高错误率，backlog参数的调节[issue](https://github.com/defineYIDA/LWebServer/issues/4)
- NIO下对Socket实现长连接(keep-alive)，并设置心跳定时清理
- 分析多余的socket连接[issue](https://github.com/defineYIDA/LWebServer/issues/3)

## 准备添加的功能：

- SSL
- limtlatch
- 辅Selector
## 博客
[关于专栏-------WebServer二三事](https://blog.csdn.net/define_LIN/article/details/89040929)

[WebServer二三事(一)Socket编程说起](https://blog.csdn.net/define_LIN/article/details/89304687)

[WebServer二三事(二)五种网络I/O模式](https://blog.csdn.net/define_LIN/article/details/89705770)

[同步/异步，阻塞/非阻塞你真的理解了吗？](https://blog.csdn.net/define_LIN/article/details/89724421)
