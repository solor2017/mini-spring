package com.hcq.framework.webmvc.view;

import lombok.Data;

import java.util.Map;

/**
 * @Author: solor
 * @Since: 2019/6/19 00:37
 * @Description:
 */
@Data
public class MyModelAndView {

    private String viewName;
    private Map <String,?> model;

    public MyModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public MyModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
