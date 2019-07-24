package com.hcq.framework.beans.config;

import lombok.Data;

/**
 * @Author: solor
 * @Since: 2019/6/17 00:21
 * @Description: bean的描述
 */
@Data
public class MyBeanDefinition {
    private String beanClassName;
    private boolean isLazyInit = false;
    private String factoryBeanName;

}
