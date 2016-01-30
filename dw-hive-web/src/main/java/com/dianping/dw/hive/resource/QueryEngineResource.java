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
import com.dianping.dw.hive.exception.LionException;
import com.dianping.dw.hive.model.UserPersonalizationInfo;
import com.dianping.dw.hive.security.HWSecurityContext;
import com.dianping.dw.hive.security.User;
import com.dianping.dw.hive.service.PersonalizationService;
import com.dianping.dw.polestar.remote.constant.ExecModeEnum;

/**
 * 查询引擎资源类
 * 
 * @author yujie.yao
 * @author tao.meng
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Path("/queryEngine")
@Produces(MediaType.APPLICATION_JSON)
public class QueryEngineResource {

    @Autowired
    private PersonalizationService personalizationService;

    @Context
    private ContainerRequestContext requestContext;

    @GET
    public List<String> getAllQueryEngines() {
        List<String> ret = Lists.newArrayList();
        for (ExecModeEnum obj : ExecModeEnum.values()) {
            ret.add(obj.value());
        }
        return ret;
    }

    @GET
    @Path("/default")
    public String getDefaultQueryEngine() throws LionException {
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();

        return personalizationService.query(user.getAdminId(), UserPersonalizationEnum.QUERY_ENGINE);
    }

    @POST
    @Path("/save")
    public int save(UserPersonalizationInfo info) {
        HWSecurityContext context = (HWSecurityContext) requestContext.getSecurityContext();
        User user = (User) context.getUserPrincipal();

        Date currentTime = new Date();
        return personalizationService.save(user.getAdminId(), UserPersonalizationEnum.QUERY_ENGINE, info.getValue(), currentTime);
    }

}
