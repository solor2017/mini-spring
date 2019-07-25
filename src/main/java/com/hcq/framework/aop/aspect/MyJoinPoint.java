package com.hcq.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public interface MyJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
