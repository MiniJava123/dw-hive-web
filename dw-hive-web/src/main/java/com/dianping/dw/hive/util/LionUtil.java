package com.dianping.dw.hive.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.dw.hive.exception.LionException;
import com.dianping.lion.client.ConfigCache;

/**
 * Lion工具类
 * 
 * @author yujie.yao
 */
public class LionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LionUtil.class);

    /**
     * 获取String属性
     * 
     * @param key
     * @return String
     * @throws LionException 
     */
    public static String getProperty(String key) {
        String exMsg = "无法获取Lion属性，key:【" + key + "】";
        try {
            String value = ConfigCache.getInstance().getProperty(key);
            if (null == value) {
                throw new LionException(exMsg);
            }
            LOG.info(key + "=" + value);
            return value;
        } catch (Exception ex) {
            LOG.error(exMsg, ex);
            throw new LionException(exMsg, ex);
        }
    }

    /**
     * 获取Integer属性
     * 
     * @param key
     * @return Integer
     * @throws LionException 
     */
    public static Integer getIntProperty(String key) {
        String exMsg = "无法获取Lion属性，key:【" + key + "】";
        try {
            Integer value = ConfigCache.getInstance().getIntProperty(key);
            if (null == value) {
                throw new LionException(exMsg);
            }
            LOG.info(key + "=" + value);
            return value;
        } catch (Exception ex) {
            LOG.error(exMsg, ex);
            throw new LionException(exMsg, ex);
        }
    }

}
