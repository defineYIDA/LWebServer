# 通过搭建简单HTTP服务器，学习服务器原理

## 学习内容：
- IO模式
- Socket
- TCP/IP
- 框架源码(Tomcat,Spring,Netty等)
- 高级网络编程
- 线程模型

## 实现的简化功能：
- BIO NIO AIO
> NIO使用的是IO多路复用的Select机制

- HTTP Protocol
> Keep-Alive

- Servlet
> ServletContext
  Dispatcher
  
- Request&Response

- Forward&Redirect

- session&cookie

## 使用技术

基于Java BIO/NIO/AIO(NIO2)、多线程（select）、Socket网络编程、XML解析、log4j/slf4j日志

## 模式

- 当前NIO的实现参照的是Tomcat的源码

## 解决问题

- AIO的高错误率，backlog参数的调节[issue](https://github.com/defineYIDA/LWebServer/issues/4)
- NIO下对Socket实现长连接(keep-alive)，并设置心跳定时清理
- 分析多余的socket连接[issue](https://github.com/defineYIDA/LWebServer/issues/3)

## 准备添加的功能：

- SSL
- limtlatch
- APR
## 博客

