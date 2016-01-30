package com.dianping.dw.hiveweb.remote.exception;

/**
 * Hive Web参数异常
 * 
 * @author yujie.yao
 */
public class ParamException extends Exception {

    private static final long serialVersionUID = 7435002475512793667L;

    public ParamException() {
        super();
    }

    public ParamException(String message) {
        super(message);
    }

    public ParamException(Throwable cause) {
        super(cause);
    }

    public ParamException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
