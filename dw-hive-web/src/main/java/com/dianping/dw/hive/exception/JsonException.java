package com.dianping.dw.hive.exception;

/**
 * Json编码解码异常
 * 
 * @author yujie.yao
 */
public class JsonException extends RuntimeException {

    private static final long serialVersionUID = 3970879962218078611L;

    public JsonException() {
        super();
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

}
