package xyz.yzblog.core.exception;


import xyz.yzblog.core.web.response.ResultCode;

/**
 * @Author: God
 * @Email: god@yzblog.xyz
 * @Description: 服务（业务）异常
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ResultCode resultCode = ResultCode.FAILURE;

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }


    public ResultCode getResultCode() {
        return resultCode;
    }
}
