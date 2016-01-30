package com.dianping.dw.hive.exception;

/**
 * HDFS操作异常
 * 
 * @author yujie.yao
 */
public class HdfsException extends Exception {

    private static final long serialVersionUID = 2297711290418425760L;

    public HdfsException() {
        super();
    }

    public HdfsException(String message) {
        super(message);
    }

    public HdfsException(Throwable cause) {
        super(cause);
    }

    public HdfsException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
