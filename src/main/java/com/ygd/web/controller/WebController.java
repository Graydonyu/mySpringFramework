package com.ygd.web.controller;

import com.ygd.framework.annotation.MyAutowired;
import com.ygd.framework.annotation.MyController;
import com.ygd.framework.annotation.MyRequestMapping;
import com.ygd.web.service.WebService;

import javax.imageio.IIOException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MyController
@MyRequestMapping("/web")
public class WebController {

    @MyAutowired
    private WebService webService;

    @MyRequestMapping("/test")
    public void test(HttpServletResponse response) throws IOException {
        response.getWriter().write("test");
    }
}
