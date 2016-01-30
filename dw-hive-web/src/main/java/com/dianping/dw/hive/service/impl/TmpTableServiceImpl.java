package com.dianping.dw.hive.service.impl;

import com.dianping.dw.hive.service.TmpTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Statement;

/**
 *  临时表服务
 *
 *  Author: tao.meng
 */
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class TmpTableServiceImpl implements TmpTableService {
    
    public static final Logger LOG = LoggerFactory.getLogger(TmpTableServiceImpl.class);

    @Override
    public int createTmpTable(Statement statement, String createCommand) throws SQLException {
        try {
            statement.execute(createCommand);
            LOG.info("create tmp table success : " + createCommand);
        } catch (SQLException e) {
            LOG.error("create tmp table failure : " + createCommand, e);
            throw new SQLException(e);
        }
        return 0;
    }

    @Override
    public int loadDataIntoTmpTable(Statement statement, String loadDataCommand) throws SQLException {
        try {
            statement.execute(loadDataCommand);
            LOG.info("load data into table : " + loadDataCommand);
        } catch (SQLException e) {
            LOG.error("load data into table failure : " + loadDataCommand, e);
            throw new SQLException(e);
        }
        return 0;
    }
}
