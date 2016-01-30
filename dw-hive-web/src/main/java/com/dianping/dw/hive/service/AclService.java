package com.dianping.dw.hive.service;

import java.util.List;

import com.dianping.dw.hive.security.Role;

/**
 * 身份验证和权限获取
 * 
 * @author yujie.yao
 */
public interface AclService {

    /**
     * 获取用户角色信息
     * 
     * @param loginId
     * @return 用户的角色信息
     */
    List<Role> getRoleListByAdminId(Integer loginId);

}
