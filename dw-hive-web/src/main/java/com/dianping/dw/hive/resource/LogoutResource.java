package com.dianping.dw.hive.resource;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dianping.dw.hive.exception.LionException;
import com.dianping.dw.hive.filter.LoginFilter;
import com.dianping.dw.hive.util.LionUtil;

/**
 * 登出资源类
 * 
 * @author yujie.yao
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Path("/logout")
@Produces(MediaType.APPLICATION_JSON)
public class LogoutResource {

    @Context
    private HttpServletRequest sr;

    @Context
    private HttpServletResponse resp;

    private String LOGOUT_REDIRECT_URL;

    @PostConstruct
    public void init() throws LionException {
        LOGOUT_REDIRECT_URL = LionUtil.getProperty("dw-hive-web.logoutRedirectUrl");
    }

    @GET
    public void logout() throws Exception {
        LoginFilter.getUserMap().remove(sr.getRemoteUser());

        HttpSession session = sr.getSession();
        session.invalidate();
        
        resp.sendRedirect(LOGOUT_REDIRECT_URL);
    }

}