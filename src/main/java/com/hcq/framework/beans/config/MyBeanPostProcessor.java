package com.hcq.framework.beans.config;

/**
 * @Author: solor
 * @Since: 2019/6/17 00:26
 * @Description:在getBean之前和之后调用  beanWrapper增强
 */
public class MyBeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName){
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName){
        return bean;
    }
}
