package com.dianping.dw.hive.exception;

/**
 * 权限异常
 * 
 * @author yujie.yao
 */
public class AuthorizationException extends Exception {

    private static final long serialVersionUID = -4214098691986691753L;

    public AuthorizationException() {
        super();
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(Throwable cause) {
        super(cause);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

}
