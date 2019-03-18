package com.lws.lwebserver.core.net.base;

import com.lws.lwebserver.core.net.wrapper.SocketWrapperBase;

/**
 * @Author: zl
 * @Date: 2019/3/16 8:54
 * 处理socket请求的基类
 */
public abstract class SocketProcessorBase<S>implements Runnable {
    protected SocketWrapperBase<S> socketWrapper;

    public SocketProcessorBase(SocketWrapperBase<S> socketWrapper) {
        reset(socketWrapper);
    }

    public void reset(SocketWrapperBase<S> socketWrapper){
        this.socketWrapper = socketWrapper;
    }

    @Override
    public final void run() {
        synchronized (socketWrapper){
            if (socketWrapper.isClosed()){
                return;
            }
            doRun();
        }
    }

    protected abstract void doRun();
}
