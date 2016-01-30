package com.dianping.dw.hive.exception;

/**
 * 解码异常
 * 
 * @author yujie.yao
 */
public class DecodeException extends Exception {

    private static final long serialVersionUID = 4067100500041226992L;

    public DecodeException() {
        super();
    }

    public DecodeException(String message) {
        super(message);
    }

    public DecodeException(Throwable cause) {
        super(cause);
    }

    public DecodeException(String message, Throwable cause) {
        super(message, cause);
    }

}
