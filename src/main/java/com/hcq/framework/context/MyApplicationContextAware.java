package com.hcq.framework.context;

/**
 * @Author: solor
 * @Since: 2019/6/17 18:49
 * @Description:可看成对第三方框架集成使用的
 */
public interface MyApplicationContextAware {

    void setApplicationContext(MyApplicationContext myApplicationContext);
}
