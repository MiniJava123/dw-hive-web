package com.dianping.dw.hive.resource;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import jersey.repackaged.com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dianping.dw.hive.constant.UserPersonalizationEnum;
import com.dianping.dw.hive.model.UserPersonalizationInfo;
import com.dianping.dw.hive.security.HWSecurityContext;
import com.dianping.dw.hive.security.Role;
import com.dianping.dw.hive.security.User;
import com.dianping.dw.hive.service.PersonalizationService;

/**
 * 角色资源类
 * 
 * @author yujie.yao
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Path("/role")
@Produces(MediaType.APPLICATION_JSON)
public class RoleResource {

    @Context
    private ContainerRequestContext requestContext;

    @Autowired
    private PersonalizationService personalizationService;

    @GET
    public List<String> getAll() {
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();

        List<String> ret = Lists.newArrayList();
        List<Role> roleList = user.getRoleList();
        for (Role role : roleList) {
            ret.add(role.getUserName());
        }
        return ret;
    }

    @GET
    @Path("/default")
    public String getDefault() {
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();

        return personalizationService.query(user.getAdminId(), UserPersonalizationEnum.ROLE);
    }

    @POST
    @Path("/save")
    public int save(UserPersonalizationInfo info) {
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();

        Date currentTime = new Date();
        return personalizationService.save(user.getAdminId(), UserPersonalizationEnum.ROLE, info.getValue(), currentTime);
    }

}
