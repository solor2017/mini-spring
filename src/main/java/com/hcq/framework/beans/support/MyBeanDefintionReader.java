package com.hcq.framework.beans.support;

import com.hcq.framework.beans.config.MyBeanDefinition;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Author: solor
 * @Since: 2019/6/17 00:29
 * @Description:
 */
@Slf4j
public class MyBeanDefintionReader {

    //存放扫描的class
    private List<String> registyBeanClasses = new ArrayList<String>();

    //固定配置文件中的key，相对于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";
    private Properties properties = new Properties();



    public MyBeanDefintionReader(String... configLocations) {

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(configLocations[0].replace("classpath:",""));
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null!=is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        doScanner(properties.getProperty(SCAN_PACKAGE));
    }

    /**
     * 注意getClassLoader().getResource()和getClass().getResource()的区别
     * 扫描配置文件中的类，放入registyBeanClasses
     * @param scanPackage
     */
    private void doScanner(String scanPackage) {

        URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.","/"));

        File classPath = new File(url.getFile());

        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage+ "." +file.getName());
            } else {
                if(!file.getName().endsWith(".class")){ continue;}
                String className = (scanPackage + "." + file.getName().replace(".class",""));
                registyBeanClasses.add(className);
                log.info("扫描类到注解的类"+className);
            }
        }

    }

    /**
     * 将扫描到的类构建成beanDefintion，并保存都list
     * @return
     */
    public List<MyBeanDefinition> loadBeanDefinitions(){
        
        List<MyBeanDefinition> beanDefinitions = new ArrayList<MyBeanDefinition>();

        for (String  className : registyBeanClasses) {

            try {
                Class<?> clzz = Class.forName(className);
                if (clzz.isInterface()) {
                    continue;
                }
                beanDefinitions.add(doCreateBeanDefinition(toLowerFirstCase(clzz.getSimpleName()),clzz.getName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }


        return beanDefinitions;

    }

    //把每一个配信息解析成一个BeanDefinition
    private MyBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName){
        MyBeanDefinition beanDefinition = new MyBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    /**
     * s首字母小写
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getConfig(){
        return this.properties;
    }
}
