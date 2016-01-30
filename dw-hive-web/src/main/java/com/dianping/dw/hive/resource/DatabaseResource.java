package com.dianping.dw.hive.resource;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dianping.dw.hive.constant.UserPersonalizationEnum;
import com.dianping.dw.hive.exception.LionException;
import com.dianping.dw.hive.model.UserPersonalizationInfo;
import com.dianping.dw.hive.security.HWSecurityContext;
import com.dianping.dw.hive.security.User;
import com.dianping.dw.hive.service.PersonalizationService;
import com.dianping.dw.hive.util.LionUtil;

/**
 * 数据库资源类
 * 
 * @author yujie.yao
 * @author tao.meng
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Path("/database")
@Produces(MediaType.APPLICATION_JSON)
public class DatabaseResource {

    @Context
    private ContainerRequestContext requestContext;

    @Autowired
    private PersonalizationService personalizationService;

    // 所有数据库
    private String ALL_DATABASES;

    @PostConstruct
    public void init() throws LionException {
        ALL_DATABASES = LionUtil.getProperty("dw-hive-web.allDatabases");
    }

    @GET
    public String[] getAll() throws LionException {
        return ALL_DATABASES.split(",");
    }

    @GET
    @Path("/default")
    public String getDefault() throws LionException {
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();

        return personalizationService.query(user.getAdminId(), UserPersonalizationEnum.DATABASE);
    }

    @POST
    @Path("/save")
    public int save(UserPersonalizationInfo info) {
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();

        Date currentTime = new Date();
        return personalizationService.save(user.getAdminId(), UserPersonalizationEnum.DATABASE, info.getValue(), currentTime);
    }

}
