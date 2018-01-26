package com.zc.server.socket.base.exceptions;

/**
 * 用于全局异常处理
 *
 * @author zing
 * @date 2018/1/20 14:56
 */
public class ServerRunTimeException extends RuntimeException {

    public ServerRunTimeException() {
    }

    public ServerRunTimeException(String message) {
        super(message);
    }

    public ServerRunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerRunTimeException(Throwable cause) {
        super(cause);
    }

    public ServerRunTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
