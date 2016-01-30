package com.dianping.dw.hive.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dianping.dw.hive.security.HWSecurityContext;
import com.dianping.dw.hive.security.User;
import com.dianping.dw.hive.service.PersonalizationService;

/**
 * 当前用户资源类
 *
 * @author yujie.yao
 * @author tao.meng
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Path("/currentUser")
@Produces(MediaType.APPLICATION_JSON)
public class CurrentUserResource {

    @Context
    private HttpServletRequest sr;

    @Context
    private ContainerRequestContext requestContext;

    @Autowired
    private PersonalizationService personalizationService;

    @GET
    @Path("/employeeCnName")
    public String getLoginUserName() {
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();
        return user.getEmployeeCnName();
    }

}
