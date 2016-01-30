package com.dianping.dw.hive.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dianping.dw.hive.constant.UserPersonalizationEnum;
import com.dianping.dw.hive.mapper.UserPersonalizationDOMapper;
import com.dianping.dw.hive.model.UserPersonalizationDO;
import com.dianping.dw.hive.model.UserPersonalizationDOExample;
import com.dianping.dw.hive.service.PersonalizationService;
import com.google.common.base.Strings;

/**
 * 用户个性化信息存取接口 实现
 *
 * @author yujie.yao
 */
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class PersonalizaionServiceImpl implements PersonalizationService {

    @Autowired
    private UserPersonalizationDOMapper userPersonalizationDOMapper;

    @Override
    public String query(Integer adminId, UserPersonalizationEnum type) {
        UserPersonalizationDOExample userPersonalizationDOExample = new UserPersonalizationDOExample();
        userPersonalizationDOExample.or().andAdminIdEqualTo(adminId).andTypeEqualTo(type.value());
        userPersonalizationDOExample.setOrderByClause("ID desc");
        List<UserPersonalizationDO> userPersonalizationDOList = userPersonalizationDOMapper.selectByExample(userPersonalizationDOExample);
        if (null == userPersonalizationDOList || userPersonalizationDOList.isEmpty()) {
            return "";
        }
        return userPersonalizationDOList.get(0).getValue();
    }

    @Override
    @Transactional
    public int save(Integer adminId, UserPersonalizationEnum type, String value, Date currentTime) {
        boolean exists = !Strings.isNullOrEmpty(query(adminId, type));
        if (exists) {
            return update(adminId, type, value, currentTime);
        } else {
            return insert(adminId, type, value, currentTime);
        }
    }

    private int insert(Integer adminId, UserPersonalizationEnum type, String value, Date currentTime) {
        UserPersonalizationDO userPersonalizationDO = new UserPersonalizationDO();
        userPersonalizationDO.setAddTime(currentTime);
        userPersonalizationDO.setUpdateTime(currentTime);
        userPersonalizationDO.setAdminId(adminId);
        userPersonalizationDO.setType(type.value());
        userPersonalizationDO.setValue(value);
        return userPersonalizationDOMapper.insert(userPersonalizationDO);
    }

    private int update(Integer adminId, UserPersonalizationEnum type, String value, Date currentTime) {
        UserPersonalizationDOExample userPersonalizationDOExample = new UserPersonalizationDOExample();
        userPersonalizationDOExample.or().andAdminIdEqualTo(adminId).andTypeEqualTo(type.value());

        UserPersonalizationDO userPersonalizationDO = new UserPersonalizationDO();
        userPersonalizationDO.setUpdateTime(currentTime);
        userPersonalizationDO.setValue(value);
        return userPersonalizationDOMapper.updateByExampleSelective(userPersonalizationDO, userPersonalizationDOExample);
    }

}
