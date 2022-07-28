package com.kk.ddd.support.exception;

/**
 *
 * <br/>
 *
 * @author KaiKoo
 */
public class MQException extends RuntimeException {

    public MQException() {
    }

    public MQException(String message) {
        super(message);
    }

    public MQException(String message, Throwable cause) {
        super(message, cause);
    }

    public MQException(Throwable cause) {
        super(cause);
    }
}
