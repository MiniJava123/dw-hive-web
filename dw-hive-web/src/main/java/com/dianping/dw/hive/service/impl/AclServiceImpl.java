package com.dianping.dw.hive.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.dianping.dw.hive.exception.JsoupException;
import com.dianping.dw.hive.exception.LionException;
import com.dianping.dw.hive.security.Role;
import com.dianping.dw.hive.service.AclService;
import com.dianping.dw.hive.util.LionUtil;
import com.google.common.collect.Lists;

/**
 * 身份验证和权限获取
 * 
 * @author yujie.yao
 */
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AclServiceImpl implements AclService {

    private static final Logger LOG = LoggerFactory.getLogger(AclServiceImpl.class);

    // 获取角色列表信息
    private String AUTH_GET_ROLE_LIST_URL;

    // ACL中的系统token
    private String AUTH_ACCESS_TOKEN;

    // 请求超时时间
    private Integer AUTH_TIMEOUT;

    /**
     * Lion中获取AUTH相关的常量
     * @throws LionException 
     */
    @PostConstruct
    public void init() throws LionException {
        AUTH_GET_ROLE_LIST_URL = LionUtil.getProperty("dw-hive-web.auth.getRoleList.url");
        AUTH_ACCESS_TOKEN = LionUtil.getProperty("dw-hive-web.auth.access.token");
        AUTH_TIMEOUT = LionUtil.getIntProperty("dw-hive-web.auth.timeout");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Role> getRoleListByAdminId(Integer adminId) {
        // 0. params check
        checkNotNull(adminId);

        List<Role> ret = Lists.newArrayList();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json =
                    Jsoup.connect(AUTH_GET_ROLE_LIST_URL).ignoreContentType(true)
                            .timeout(AUTH_TIMEOUT).data("login_id", adminId.toString())
                            .data("access_token", AUTH_ACCESS_TOKEN)
                            .method(Method.GET).execute().body();
            LOG.info(adminId + ":【" + json + "】");

            Map<String, Object> maps = objectMapper.readValue(json, Map.class);

            List<Map<String, Object>> msg = (List<Map<String, Object>>) maps.get("msg");
            for (Map<String, Object> map : msg) {
                Integer usageType = (Integer) map.get("usage_type");
                String userName = (String) map.get("user_name");
                String password = (String) map.get("passwd");

                // usageType: 0-线上, 1-线下, 2-MySQL
                if (1 == usageType) {
                    Role role = new Role();
                    role.setUserName(userName);
                    role.setPassword(password);
                    ret.add(role);
                }
            }
            LOG.info(adminId + "共有【" + ret.size() + "】个可用的线下账号");
        } catch (IOException ex) {
            String exMsg = "获取用户信息失败";
            LOG.error(exMsg, ex);
            throw new JsoupException(exMsg, ex);
        }
        return ret;
    }

}
