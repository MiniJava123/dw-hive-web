package com.dianping.dw.hive.model;

import java.util.Date;

import com.dianping.dw.hive.util.CommonUtil;

public class UserPersonalizationDO {

    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column HW_UserPersonalization.ID
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    private Integer id;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column HW_UserPersonalization.AddTime
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    private Date addTime;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column HW_UserPersonalization.UpdateTime
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    private Date updateTime;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column HW_UserPersonalization.AdminID
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    private Integer adminId;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column HW_UserPersonalization.Type
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    private String type;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column HW_UserPersonalization.Value
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    private String value;

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column HW_UserPersonalization.ID
     * @return  the value of HW_UserPersonalization.ID
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column HW_UserPersonalization.ID
     * @param id  the value for HW_UserPersonalization.ID
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column HW_UserPersonalization.AddTime
     * @return  the value of HW_UserPersonalization.AddTime
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    public Date getAddTime() {
        return addTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column HW_UserPersonalization.AddTime
     * @param addTime  the value for HW_UserPersonalization.AddTime
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column HW_UserPersonalization.UpdateTime
     * @return  the value of HW_UserPersonalization.UpdateTime
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column HW_UserPersonalization.UpdateTime
     * @param updateTime  the value for HW_UserPersonalization.UpdateTime
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column HW_UserPersonalization.AdminID
     * @return  the value of HW_UserPersonalization.AdminID
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    public Integer getAdminId() {
        return adminId;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column HW_UserPersonalization.AdminID
     * @param adminId  the value for HW_UserPersonalization.AdminID
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column HW_UserPersonalization.Type
     * @return  the value of HW_UserPersonalization.Type
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    public String getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column HW_UserPersonalization.Type
     * @param type  the value for HW_UserPersonalization.Type
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column HW_UserPersonalization.Value
     * @return  the value of HW_UserPersonalization.Value
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    public String getValue() {
        return value;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column HW_UserPersonalization.Value
     * @param value  the value for HW_UserPersonalization.Value
     * @mbggenerated  Tue Dec 16 13:40:37 CST 2014
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return CommonUtil.toJson(this);
    }

}