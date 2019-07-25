package com.hcq.framework.aop.config;

import lombok.Data;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
@Data
public class MyAopConfig {

    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;

}
