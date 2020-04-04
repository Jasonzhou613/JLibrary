package com.ttsea.jlibrary.exceptions;

/**
 * // to do <br>
 * <p>
 * <b>date:</b> 2017/7/31 10:42 <br>
 * <b>author:</b> zhijian.zhou <br>
 * <b>version:</b> 1.0 <br>
 */
@SuppressWarnings("serial")
public class BaseException extends RuntimeException {
    public BaseException() {
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public static String parserError(Throwable e) {
        String errorMsg = "";
        String clazzName = e.getClass().getName();

        if (clazzName.contains("SocketTimeoutException")) {
            errorMsg = "Socket timeout";
        }

        if (errorMsg == null || errorMsg.length() == 0) {
            errorMsg = e.getMessage();
        }

        if (errorMsg == null || errorMsg.length() == 0) {
            errorMsg = "unknow error";
        }

        return errorMsg;
    }
}
