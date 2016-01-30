package com.dianping.dw.hive.constant;

import java.util.HashMap;
import java.util.Map;

import com.dianping.dw.hive.exception.EnumTypeException;

/**
 * 用户个性化类型
 *
 * @author tao.meng
 */
public enum UserPersonalizationEnum {

    DATABASE("DATABASE"),
    QUERY_ENGINE("QUERY_ENGINE"),
    ROLE("ROLE");

    private final String userPersonalization;

    private UserPersonalizationEnum(String userPersonalization) {
        this.userPersonalization = userPersonalization;
    }

    private static final Map<String, UserPersonalizationEnum> userPersonalizationMap = new HashMap<String, UserPersonalizationEnum>();

    static {
        for (UserPersonalizationEnum userPersonalizationType : UserPersonalizationEnum.values()) {
            userPersonalizationMap.put(userPersonalizationType.value(), userPersonalizationType);
        }
    }

    public String value() {
        return this.userPersonalization;
    }

    public static UserPersonalizationEnum fromValue(String value) {
        UserPersonalizationEnum userPersonalization = userPersonalizationMap.get(value);
        if (null == userPersonalization) {
            throw new EnumTypeException("用户个性化类型错误，value:【" + value + "】");
        }
        return userPersonalization;
    }

    @Override
    public String toString() {
        return this.userPersonalization;
    }

}
