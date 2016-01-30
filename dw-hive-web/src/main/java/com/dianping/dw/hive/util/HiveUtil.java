package com.dianping.dw.hive.util;

import com.dianping.dw.hive.model.CreateTableInfoDO;
import com.dianping.dw.hive.model.LoadTableInfoDO;
import com.dianping.dw.hive.security.HWSecurityContext;
import com.dianping.dw.hive.security.Role;
import com.dianping.dw.hive.security.User;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.security.Krb5Login;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * hive 工具类
 *
 * Author: tao.meng
 */
public class HiveUtil {
    public static final Logger LOG = LoggerFactory.getLogger(HiveUtil.class);

    /**
     * build创建临时表的命令
     *
     * @param tableInfo
     * @return String
     */
    public static String buildCreateTableSQL(CreateTableInfoDO tableInfo) {
        StringBuilder hql = new StringBuilder();
        hql.append("CREATE ");
        if (tableInfo.getExternal()) {
            hql.append("EXTERNAL ");
        }
        hql.append("TABLE IF NOT EXISTS `").append(
//                tableInfo.getDatabaseName()).append(".").append(tableInfo.getTableName()
                tableInfo.getTableName()
            ).append("`\n(\n");

        for (int i = 0; i < tableInfo.getColumnList().size(); i++) {
            Map<String, String> col = tableInfo.getColumnList().get(i);
            
            String columnName = col.get("columnName");
            String columnType = col.get("columnType");
            String columnComment = col.get("columnComment");

            hql.append("  ").append(columnName).append("    ").append(columnType)
                    .append("    ").append("COMMENT").append("  '").append(columnComment)
                    .append("'");
            
            if ( i != (tableInfo.getColumnList().size()-1) ) {
                hql.append(",");
            }
            hql.append("\n");
        }
        hql.append(")\n");
        if (StringUtils.isNotBlank(tableInfo.getTableComment())) {
            hql.append("COMMENT '").append(tableInfo.getTableComment()).append("'\n");
        }
        hql.append("ROW FORMAT DELIMITED\n FIELDS TERMINATED BY '")
                .append(tableInfo.getColSeparator()).append("'\n LINES TERMINATED BY '\\n'\n")
                .append("STORED AS ");
        if ("InputFormat".equalsIgnoreCase(tableInfo.getFileFormat())) {
            hql.append("\n  INPUTFORMAT '").append(tableInfo.getInputFormat())
                    .append("'\n  OUTPUTFORMAT  '").append(tableInfo.getOutputFormat())
                    .append("'\n");
        } else {
            hql.append(tableInfo.getFileFormat()).append("\n");
        }
        if (StringUtils.isNotBlank(tableInfo.getTableStoragePath())) {
            hql.append("LOCATION '").append(tableInfo.getTableStoragePath()).append("'");
        }
        return hql.toString();
    }

    /**
     * build加载数据到临时表的命令
     *
     * @param loadTableInfo
     * @param dataPath
     * @return String
     */
    public static String buildLoadDataIntoTableSQL(LoadTableInfoDO loadTableInfo, String dataPath) {
        StringBuilder hql = new StringBuilder();
        hql.append("LOAD DATA INPATH '")
                .append(dataPath)
                .append("' ");
        if (loadTableInfo.isOverWrite()) {
            hql.append("OVERWRITE ");
        }
        hql.append("INTO TABLE ")
                .append(loadTableInfo.getDatabaseName()).append(".").append(loadTableInfo.getTableName());
        if (StringUtils.isNotBlank(loadTableInfo.getPartition())) {
            hql.append(" PARTITION (").append(loadTableInfo.getPartition()).append(")");
        }

        return hql.toString();
    }

    /**
     * 获取hive的连接 statement
     *
     * @param context
     * @param roleName
     * @return Statement
     */
    public static Statement getConnectStatement(HWSecurityContext context, String roleName)
            throws ClassNotFoundException, SQLException, LoginException {
        Statement statement = null;

        // 1. 获取用户登录role信息
        User user = (User) context.getUserPrincipal();
        Role currentRole = CommonUtil.getCurrentRole(roleName, user.getRoleList());

        // 2. 通过kerberos认证 并 获取 hive server2 连接
        try {
            UserGroupInformation ugi = Krb5Login.getVerifiedUgi(currentRole.getUserName(), currentRole.getPassword());
            Connection connection = HiveJdbcClient.getConnection(currentRole.getUserName(), ugi);
            if (null != connection) {
                statement = connection.createStatement();    
            } else {
                return null;
            }
        } catch (LoginException e) {
            LOG.error("krb5login failure , username : " + roleName);
            throw new LoginException();
        }
        return statement;
    }
}
