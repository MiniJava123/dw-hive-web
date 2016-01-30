package com.dianping.dw.hive;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.beanvalidation.MvcBeanValidationFeature;
import org.springframework.web.filter.RequestContextFilter;

/**
 * 注册器
 * 
 * @author yujie.yao
 */
public class Application extends ResourceConfig {

    /**
     * 注册JAX-RS组件
     */
    public Application() {
        super(RequestContextFilter.class, JacksonFeature.class, MvcBeanValidationFeature.class);
    }

}
