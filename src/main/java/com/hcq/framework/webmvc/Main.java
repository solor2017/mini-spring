package com.hcq.framework.webmvc;

import com.hcq.demo.action.MyAction;
import com.hcq.framework.context.MyApplicationContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: solor
 * @Since: 2019/6/17 14:05
 * @Description:
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        MyApplicationContext myApplicationContext = new MyApplicationContext("classpath:application.properties");

        try {
            Object object = myApplicationContext.getBean("myAction");
            System.out.println(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
