package com.hcq.framework.context;

import com.hcq.framework.annotation.MyController;
import com.hcq.framework.annotation.MyService;
import com.hcq.framework.beans.MyBeanWrapper;
import com.hcq.framework.beans.config.MyBeanDefinition;
import com.hcq.framework.beans.support.MyBeanDefintionReader;
import com.hcq.framework.beans.support.MyDefaultListableBeanFactory;
import com.hcq.framework.core.MyBeanFctory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: solor
 * @Since: 2019/6/17 01:23
 * @Description: 模拟spring初始化bean，完成IOC和DI
 */
@Slf4j
public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFctory {

    private String [] configLoactions;
    private MyBeanDefintionReader reader;
    //存放注册式单例的容器
    private Map<String,Object> singletonBeanCacheMap = new HashMap<String, Object>();
    //真正的IOC容器
    private Map<String, MyBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, MyBeanWrapper>();


    public MyApplicationContext(String... configLocations) {

        this.configLoactions = configLocations;
        refresh();
    }

    /**
     * 继承自MyAbstractApplicationContext
     */
    @Override
    public void refresh() {

        //1. 定位
        MyBeanDefintionReader myBeanDefintionReader = new MyBeanDefintionReader(this.configLoactions);
        log.info("配置文件读取完成");
        //2.加载
        List<MyBeanDefinition> myBeanDefinitions = myBeanDefintionReader.loadBeanDefinitions();
        //3.注册
        try {
            doRegisterBeanDefinition(myBeanDefinitions);
            log.info("扫描并完成Bean装配完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("IOC完成");
        doAutowrited();
        log.info("DI完成");
    }

    /**
     * 不需要延时加载的类，完成DI
     */
    private void doAutowrited() {
        for (Map.Entry<String, MyBeanDefinition> myBeanDefinitionEntry : beanDefinitionMap.entrySet()) {
            String beanName = myBeanDefinitionEntry.getKey();
            if (!myBeanDefinitionEntry.getValue().isLazyInit()) {
                getBean(beanName);
            }
        }

    }

    /**
     * myBeanDefinitions注册进map(IOC)
     * @param myBeanDefinitions
     */
    private void doRegisterBeanDefinition(List<MyBeanDefinition> myBeanDefinitions) throws Exception {

        for (MyBeanDefinition myBeanDefinition : myBeanDefinitions) {
            if (beanDefinitionMap.containsKey(myBeanDefinition.getFactoryBeanName())) {
                throw new Exception("The “" + myBeanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            beanDefinitionMap.put(myBeanDefinition.getFactoryBeanName(),myBeanDefinition);
        }


    }

    @Override
    public Object getBean(String beanName) {


        /**
         * 在spring中，如果bean实现了InitAware接口
         * spring在instantiateBean()之前和之后会调用BeanPostProcessor接口的postProcessBeforeInitialization和
         *  postProcessAfterInitialization。参考mybatis的sessionFactory实现InitializingBean机制
         */
        //1.初始化
        MyBeanDefinition myBeanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = instantiateBean(myBeanDefinition);
        if (instance==null) {
            return null;
        }


        //2.把instance放入beanWrapper，再把beanWrapper放入真正的IOC容器
        MyBeanWrapper beanWrapper = new MyBeanWrapper(instance);
        /**
         * 区分单例bean和property的
         *
         */
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);
        //3.完成DI操作
        populateBean(beanName,new MyBeanDefinition(),beanWrapper);
        return factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    /**
     * 完成IOC容器中bean属性的DI操作
     * @param beanName
     * @param myBeanDefinition
     * @param beanWrapper
     */
    private void populateBean(String beanName, MyBeanDefinition myBeanDefinition, MyBeanWrapper beanWrapper) {

        Object instanceClass = beanWrapper.getWrappedInstance();
        Class<?> wrappedClass = beanWrapper.getWrappedClass();
        if (!(wrappedClass.isAnnotationPresent(MyController.class) ||
                wrappedClass.isAnnotationPresent(MyService.class))) {
            return;
        }


    }

    /**
     * 默认bean是单例
     * myBeanDefinition返回实例bean
     * @param myBeanDefinition
     * @return
     */
    private Object instantiateBean(MyBeanDefinition myBeanDefinition) {

        Object instance = null;
        String className = myBeanDefinition.getBeanClassName();
        if (singletonBeanCacheMap.containsKey(className)) {
            instance = this.singletonBeanCacheMap.get(className);
            return instance;
        } else {
            try {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.singletonBeanCacheMap.put(className,instance);
                this.singletonBeanCacheMap.put(myBeanDefinition.getFactoryBeanName(),instance);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            return instance;
        }

    }

    @Override
    public Object getBean(Class<?> beanClass) {
        return getBean(beanClass.getName());
    }

    /**
     * 获取容器中所有的bean
     * @return
     */
    public String[] getBeanDefinitionNames() {
        Set<String> strings = singletonBeanCacheMap.keySet();
        String[] beanDefinitionNames = strings.toArray(new  String[this.beanDefinitionMap.size()]);
        return beanDefinitionNames;
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }
}
