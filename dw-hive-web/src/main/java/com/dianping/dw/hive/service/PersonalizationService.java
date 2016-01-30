package com.dianping.dw.hive.service;

import java.util.Date;

import com.dianping.dw.hive.constant.UserPersonalizationEnum;

/**
 * 用户个性化信息存取接口
 *
 * @author tao.meng
 */
public interface PersonalizationService {

    /**
     * 查询用户个性化信息
     * 
     * @param adminId
     * @param type
     * @return
     */
    String query(Integer adminId, UserPersonalizationEnum type);

    /**
     * 保存用户个性化信息
     * 
     * @param adminId
     * @param type
     * @param value
     * @param currentTime
     * @return
     */
    int save(Integer adminId, UserPersonalizationEnum type, String value, Date currentTime);

}