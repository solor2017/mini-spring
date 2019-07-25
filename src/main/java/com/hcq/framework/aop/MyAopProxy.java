package com.hcq.framework.aop;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public interface MyAopProxy {
    Object getProxy();


    Object getProxy(ClassLoader classLoader);
}
