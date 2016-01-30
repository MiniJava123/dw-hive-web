package com.dianping.dw.hive.model;

/**
 * 用户的单条个性化信息k-v对
 *
 * @author tao.meng
 */
public class UserPersonalizationInfo {

    private String type;
    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}