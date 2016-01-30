package com.dianping.dw.hive.exception;

/**
 * Jsoup请求异常
 * 
 * @author yujie.yao
 */
public class JsoupException extends RuntimeException {

    private static final long serialVersionUID = -2399103629514881643L;

    public JsoupException() {
        super();
    }

    public JsoupException(String message) {
        super(message);
    }

    public JsoupException(Throwable cause) {
        super(cause);
    }

    public JsoupException(String message, Throwable cause) {
        super(message, cause);
    }

}
