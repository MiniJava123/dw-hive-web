package com.dianping.dw.hive.util;

import com.dianping.dw.hive.exception.LionException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * hive server2 连接与操作API
 *
 * Author: tao.meng
 */
public class HiveJdbcClient {
    private static final Logger LOG = LoggerFactory.getLogger(HiveJdbcClient.class);

    private static String hiveDriverName;
    private static String hiveServer2Url;
    private static Object lockObject = new Object();
    private static Cache<String, Connection> jdbcConnectCache = CacheBuilder
            .newBuilder().concurrencyLevel(4).maximumSize(100000)
            .expireAfterAccess(24, TimeUnit.HOURS)
            .removalListener(new RemovalListener<String, Connection>() {
                @Override
                public void onRemoval(RemovalNotification<String, Connection> rn) {
                    LOG.info("username: " + rn.getKey() + " Connection was removed from jdbcConnectCache");
                }
            }).build();
    
    static {
        try {
            hiveDriverName =  LionUtil.getProperty("dw-hive-web.hiveDriverName");
            hiveServer2Url = LionUtil.getProperty("dw-hive-web.hiveServer2Url");
        } catch (LionException e) {
            LOG.error("get key-value from lion failure , ", e);
        }
        try {
            Class.forName(hiveDriverName);
        } catch (ClassNotFoundException cfe) {
            LOG.error("Hive Driver Not Found " + hiveDriverName, cfe);
        }
    }

    /**
     *  获取hive server2 的jdbc链接
     *
     *  @param username
     *  @param ugi 经过认证的ugi信息
     * */
    public static Connection getConnection(String username, final UserGroupInformation ugi) {
        Connection connection;
        synchronized (lockObject) {
            connection = jdbcConnectCache.getIfPresent(username);
            if (null == connection) {
                connection = _getConnection(ugi);
                jdbcConnectCache.put(username, connection);
            }    
        }
        return connection;
    }

    private static Connection _getConnection(final UserGroupInformation ugi) {
        Connection connection = null;
        if (null != ugi) {
            connection = ugi.doAs(new PrivilegedAction<Connection>() {
                @Override
                public Connection run() {
                    Connection con = null;
                    try {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("start get connection ugi username:" + ugi.getUserName());
                        }
                        con = DriverManager.getConnection(hiveServer2Url, "", "");
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("through get connection ugi username:" + ugi.getUserName()) ;
                        }
                    } catch (SQLException e) {
                        LOG.error("get connection failed：" + e.getMessage());
                    }
                    return con;
                }
            });
        }
        return connection;
    }
}
