package com.hcq.framework.aop.intercept;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public interface MyMethodInterceptor {
    Object invoke(MyMethodInvocation invocation) throws Throwable;
}
