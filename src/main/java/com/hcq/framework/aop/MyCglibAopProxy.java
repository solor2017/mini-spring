package com.hcq.framework.aop;


import com.hcq.framework.aop.support.MyAdvisedSupport;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class MyCglibAopProxy implements  MyAopProxy {
    public MyCglibAopProxy(MyAdvisedSupport config) {
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
