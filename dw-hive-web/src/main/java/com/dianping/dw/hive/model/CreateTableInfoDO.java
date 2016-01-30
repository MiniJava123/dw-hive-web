package com.dianping.dw.hive.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

/**
 * 创建临时表的信息
 *
 * Author: tao.meng
 */
@XmlRootElement
public class CreateTableInfoDO {
    private String databaseName;
    private String tableName;
    private String tableComment;
    private String colSeparator;
    @JsonDeserialize( as = Boolean.class )
    private Boolean isExternal;
    private String tableStoragePath;
    private String fileFormat;
    private String inputFormat;
    private String outputFormat;
    private List<Map<String,String>> columnList;

    private String createTableSql;

    public String getDatabaseName() {
        return databaseName;
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

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public String getColSeparator() {
        return colSeparator;
    }

    public void setColSeparator(String solSeparator) {
        this.colSeparator = solSeparator;
    }

    public boolean getExternal() {
        return isExternal;
    }

    public void setExternal(boolean isExternal) {
        this.isExternal = isExternal;
    }

    public String getTableStoragePath() {
        return tableStoragePath;
    }

    public void setTableStoragePath(String tableStoragePath) {
        this.tableStoragePath = tableStoragePath;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getCreateTableSql() {
        return createTableSql;
    }

    public void setCreateTableSql(String createTableSql) {
        this.createTableSql = createTableSql;
    }

    public List<Map<String, String>> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Map<String, String>> columnList) {
        this.columnList = columnList;
    }
}
