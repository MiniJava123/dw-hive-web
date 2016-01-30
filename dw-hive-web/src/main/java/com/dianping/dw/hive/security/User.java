package com.dianping.dw.hive.security;

import java.security.Principal;
import java.util.List;

import com.dianping.dw.hive.util.CommonUtil;

/**
 * 用户身份认证类
 * 
 * @author yujie.yao
 */
public class User implements Principal {

    private Integer adminId;

    private String employeeId;

    private String employeeEnName;

    private String employeeCnName;

    private String employeeEmail;

    /**
     * 角色列表
     */
    private List<Role> roleList;

    @Override
    public String getName() {
        return String.valueOf(adminId);
    }

    @Override
    public String toString() {
        return CommonUtil.toJson(this);
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeEnName() {
        return employeeEnName;
    }

    public void setEmployeeEnName(String employeeEnName) {
        this.employeeEnName = employeeEnName;
    }

    public String getEmployeeCnName() {
        return employeeCnName;
    }

    public void setEmployeeCnName(String employeeCnName) {
        this.employeeCnName = employeeCnName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

}
