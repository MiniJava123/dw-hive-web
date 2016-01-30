package com.dianping.dw.hive.model;

/**
 * 建表语句信息
 * 
 * Author: tao.meng
 */
public class HqlInfoDO {
    private String roleName;
    private String hql;
    private String database;

    public String getHql() {
        return hql;
    }

    public void setHql(String hql) {
        this.hql = hql;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
