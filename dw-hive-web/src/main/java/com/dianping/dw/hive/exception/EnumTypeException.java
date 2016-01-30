package com.dianping.dw.hive.exception;

/**
 * 枚举值异常
 */
public class EnumTypeException extends RuntimeException {

    private static final long serialVersionUID = 484440998492372810L;

    public EnumTypeException() {
        super();
    }

    public EnumTypeException(String message) {
        super(message);
    }

    public EnumTypeException(Throwable cause) {
        super(cause);
    }

    public EnumTypeException(String message, Throwable cause) {
        super(message, cause);
    }

}