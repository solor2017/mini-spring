package com.hcq.framework.webmvc.handler;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @Author: solor
 * @Since: 2019/6/18 21:42
 * @Description:
 */
@Data
public class MyHandlerMapping {

    /**
     * 执行方法的Controller
     */
    private Object controller;
    /**
     * url中解析出的方法
     */
    private Method method;
    /**
     * url规则匹配
     */
    private Pattern pattern;

    public MyHandlerMapping(Pattern pattern,Object controller, Method method) {
        this.pattern = pattern;
        this.controller = controller;
        this.method = method;
    }
}
