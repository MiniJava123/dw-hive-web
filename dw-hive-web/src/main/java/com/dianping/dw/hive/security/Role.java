package com.dianping.dw.hive.security;

/**
 * 角色，存放hive的用户名和密码
 * 
 * @author yujie.yao
 */
public class Role {

    private String userName;
    private String password;

    public Role() {
        super();
    }

    public Role(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
