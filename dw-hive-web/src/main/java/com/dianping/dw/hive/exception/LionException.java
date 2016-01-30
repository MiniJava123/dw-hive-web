package com.dianping.dw.hive.exception;

/**
 * Lion异常
 * 
 * @author yujie.yao
 */
public class LionException extends RuntimeException {

    private static final long serialVersionUID = -252159661672913019L;

    public LionException() {
        super();
    }

    public LionException(String message) {
        super(message);
    }

    public LionException(Throwable cause) {
        super(cause);
    }

    public LionException(String message, Throwable cause) {
        super(message, cause);
    }

}
