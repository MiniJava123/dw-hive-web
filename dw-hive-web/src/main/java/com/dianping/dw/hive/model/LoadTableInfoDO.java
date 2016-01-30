package com.dianping.dw.hive.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 加载数据到临时表的DO
 *
 * Author: tao.meng
 */
@XmlRootElement
public class LoadTableInfoDO {
    private String roleName;
    private String databaseName;
    private String tableName;
    @JsonDeserialize( as = Boolean.class )
    private Boolean isOverWrite;
    private String partition;
    private List<String> fileList;

    public String getDatabaseName() {
        return databaseName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Boolean isOverWrite() {
        return isOverWrite;
    }

    public void setOverWrite(Boolean isOverWrite) {
        this.isOverWrite = isOverWrite;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }
}
