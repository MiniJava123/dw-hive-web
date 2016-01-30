package com.dianping.dw.hive.filter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.dw.hive.security.HWSecurityContext;
import com.dianping.dw.hive.security.User;
import com.dianping.dw.hive.service.AclService;
import com.dianping.dw.hive.service.LoginService;
import com.google.common.collect.Maps;

/**
 * 用户登录拦截器
 * 
 * @author yujie.yao
 */
@Provider
public class LoginFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoginFilter.class);

    @Context
    private HttpServletRequest sr;

    @Autowired
    private AclService aclService;

    @Autowired
    private LoginService loginService;

    private static Map<String, User> userMap = Maps.newConcurrentMap();

    private static Object mutex = new Object();
    private static Map<String, Object> userMutexMap = Maps.newConcurrentMap();

    static {
        emptyUserMapEveryHour();
    }

    /**
     * 每小时清空服务器缓存的用户信息
     */
    private static void emptyUserMapEveryHour() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                LoginFilter.getUserMap().clear();
            }
        };
        // everyday, every hour
        DateTime dt = new DateTime(2000, 1, 1, 0, 0, 0);
        // 1hr = 3600000
        timer.schedule(task, dt.toDate(), 3600000);
    }

    /**
     * 拦截方法入口
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // 从SSO获取用户信息，格式：xiaomeng.chen|-16597|0002317|陈小梦
        String ssoStr = sr.getRemoteUser();

        // 用户打开页面时会同时发送多个ajax请求到server端
        // 加锁的机制可以防止单台服务器多次调用ACL
        Object userMutex;
        // 全局锁以获取用户锁
        synchronized (mutex) {
            userMutex = userMutexMap.get(ssoStr);
            if (null == userMutex) {
                userMutex = new Object();
                userMutexMap.put(ssoStr, userMutex);
            }
        }
        // 用户锁
        synchronized (userMutex) {
            User user = userMap.get(ssoStr);
            if (null == user || user.getRoleList().isEmpty()) {
                LOG.info("ssoStr:【" + ssoStr + "】");
                user = parseUserInfo(ssoStr);
    
                // 从ACL获取用户的角色信息（角色：用户名+密码）
                user.setRoleList(aclService.getRoleListByAdminId(user.getAdminId()));
    
                // 更新userMap
                userMap.put(ssoStr, user);
                LOG.info("user:【" + user.toString() + "】");
            }
            requestContext.setSecurityContext(new HWSecurityContext(user));
        }
    }

    // 解析用户信息
    private User parseUserInfo(String ssoStr) {
        checkNotNull(ssoStr);

        // 格式：xiaomeng.chen|-16597|0002317|陈小梦
        String[] arr = ssoStr.split("\\|");
        checkArgument(4 == arr.length);

        String employeeEnName = arr[0];
        Integer adminId = Integer.parseInt(arr[1]);
        String employeeId = arr[2];
        String employeeCnName = arr[3];

        User user = new User();
        user.setEmployeeEnName(employeeEnName);
        user.setEmployeeEmail(employeeEnName + "@dianping.com");
        user.setAdminId(adminId);
        user.setEmployeeId(employeeId);
        user.setEmployeeCnName(employeeCnName);
        return user;
    }

    // 登出会获取并清空特定用户的信息，故public
    public static Map<String, User> getUserMap() {
        return userMap;
    }

    // 登出会获取并清空特定用户的信息，故public
    public static void setUserMap(Map<String, User> userMap) {
        LoginFilter.userMap = userMap;
    }

}
