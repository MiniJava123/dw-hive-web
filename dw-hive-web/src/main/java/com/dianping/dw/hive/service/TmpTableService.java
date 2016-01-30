package com.dianping.dw.hive.service;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * 临时表创建与上传数据服务
 *
 * Author: tao.meng
 */
public interface TmpTableService {
    
    /**
     * 创建临时表
     * 
     * @param createCommand
     * @return int
     * @exception
     */
    public int createTmpTable(Statement statement, String createCommand) throws SQLException;

    /**
     * 上传数据到临时表
     *
     * @param loadDataCommand
     * @return int
     * @exception
     */
    public int loadDataIntoTmpTable(Statement statement, String loadDataCommand) throws SQLException;
    
}
