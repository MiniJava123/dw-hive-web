package com.dianping.dw.hive.service;

import java.util.Date;

import com.dianping.dw.hive.security.User;

/**
 * 用户登录服务
 * 
 * @author yujie.yao
 */
public interface LoginService {

    /**
     * 更新用户登录信息
     * 
     * @param user 用户信息
     * @param currentTime 当前时间
     */
    void updateLoginInfo(User user, Date currentTime);

}
