package com.dianping.dw.hive.constant;

import java.util.HashMap;
import java.util.Map;

import com.dianping.dw.hive.exception.EnumTypeException;

/**
 * 下载类型
 *
 * @author yujie.yao
 */
public enum DownloadTypeEnum {

    CSV("CSV"),
    XLSX("XLSX");

    private final String downloadType;

    private DownloadTypeEnum(String downloadType) {
        this.downloadType = downloadType;
    }

    private static final Map<String, DownloadTypeEnum> downloadTypeMap = new HashMap<String, DownloadTypeEnum>();

    static {
        for (DownloadTypeEnum downloadType : DownloadTypeEnum.values()) {
            downloadTypeMap.put(downloadType.value(), downloadType);
        }
    }

    public String value() {
        return this.downloadType;
    }

    public static DownloadTypeEnum fromValue(String value) {
        DownloadTypeEnum downloadType = downloadTypeMap.get(value);
        if (null == downloadType) {
            throw new EnumTypeException("下载类型错误，value:【" + value + "】");
        }
        return downloadType;
    }

    @Override
    public String toString() {
        return this.downloadType;
    }

}
