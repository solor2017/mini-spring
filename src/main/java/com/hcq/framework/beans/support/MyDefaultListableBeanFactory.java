package com.hcq.framework.beans.support;

import com.hcq.framework.beans.config.MyBeanDefinition;
import com.hcq.framework.context.support.MyAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: solor
 * @Since: 2019/6/17 00:21
 * @Description:
 */
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext {

    //存储注册信息的BeanDefinition
    protected final Map<String, MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, MyBeanDefinition>();
}
