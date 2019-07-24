package com.hcq.framework.core;

/**
 * @Author: solor
 * @Since: 2019/6/17 00:16
 * @Description:
 */
public interface MyBeanFctory {

    /**
     * 获取bean，依据spring。实际获取的是一个beanWrapper
     * @param beanName
     * @return
     */
    Object getBean(String beanName);
    Object getBean(Class<?> beanClass);
}
