package com.dianping.dw.hive.security;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

/**
 * Hive Web身份验证上下文
 * 
 * @author yujie.yao
 */
public class HWSecurityContext implements SecurityContext {

    private final User user;

    public HWSecurityContext(User user) {
        this.user = user;
    }

    @Override
    public String getAuthenticationScheme() {
        return HWSecurityContext.BASIC_AUTH;
    }

    @Override
    public Principal getUserPrincipal() {
        return user;
    }

    @Override
    public boolean isSecure() {
        return (null != user) ? true : false;
    }

    @Override
    public boolean isUserInRole(String role) {
        return true;
    }

}
