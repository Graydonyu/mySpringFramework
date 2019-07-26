package com.ygd.framework.servlet;

import com.ygd.framework.annotation.MyController;
import com.ygd.framework.annotation.MyService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MyDispatcherServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    private List<String> beanNameList = new ArrayList<String>();

    private Map<String, Object> ioc = new HashMap<String, Object>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.读取配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //2.扫描包路径
        doScanner(contextConfig.getProperty("scanPackage"));

        //3.初始化所有相关的类
        doInstance();

        //4.自动注入
        doAutoWired();

        //5.初始化HandlerMapping
        initHanderMapping();
    }

    private void initHanderMapping() {
    }

    private void doAutoWired() {
        if (ioc.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            
        }
    }

    private void doInstance() {
        if (beanNameList.isEmpty()) {
            return;
        }

        try {
            for (String className : beanNameList) {
                Class<?> clazz = Class.forName(className);

                if (clazz.isAnnotationPresent(MyController.class)) {
                    String beanName = lowerFirstCase(clazz.getName());

                    ioc.put(beanName,clazz.newInstance());
                }else if(clazz.isAnnotationPresent(MyService.class)){
                    //使用注解值
                    MyService myService = clazz.getAnnotation(MyService.class);
                    String beanName = myService.value();

                    if ("".equals(beanName.trim())) {
                        beanName = lowerFirstCase(clazz.getName());
                    }

                    //service
                    ioc.put(beanName,clazz.newInstance());

                    //接口名-》实现类
                    for (Class<?> i : clazz.getInterfaces()) {
                        ioc.put(i.getName(),clazz.newInstance());
                    }
                }else{
                    continue;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String lowerFirstCase(String className) {
        char[] chars = className.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScanner(String scanPackage) {
        String scanPath = "";
        if (scanPackage != null && scanPackage.isBlank()) {
            scanPackage.replaceAll("\\.", "/");
        }
        URL url = this.getClass().getClassLoader().getResource("/" + scanPath);

        File fileDir = new File(url.getFile());

        for (File file : fileDir.listFiles()) {
            //判断是否是目录
            if (file.isDirectory()) {
                //递归调用直到完成扫描
                doScanner(scanPackage + "." + file.getName());
            } else {
                String beanName = file.getName().replace(".class","");
                beanNameList.add(beanName);
            }
        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);

        try {
            contextConfig.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
