package com.ygd.web.controller;

import com.ygd.framework.annotation.MyAutowired;
import com.ygd.framework.annotation.MyController;
import com.ygd.framework.annotation.MyRequestMapping;
import com.ygd.web.service.WebService;

@MyController
@MyRequestMapping("/web")
public class WebController {

    @MyAutowired
    private WebService webService;
}
