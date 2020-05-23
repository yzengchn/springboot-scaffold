package xyz.yzblog.core.exception;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import xyz.yzblog.core.utils.WebUtils;
import xyz.yzblog.core.web.response.R;
import xyz.yzblog.core.web.response.ResultCode;

import javax.servlet.ServletException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: God
 * @Email: god@yzblog.xyz
 * @Description: 全局异常处理
 */
@Slf4j
@Configuration
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 业务异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    public R handlerServiceException(ServiceException e){
        return R.fail(e.getResultCode(), e.getMessage());
    }

    /**
     * JSON参数校验异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handlerMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error(e.getMessage(), e);
        return R.fail(ResultCode.FAILURE, genErrorMsg(e.getBindingResult(), e.getMessage()));
    }

    /**
     * 表单参数校验异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    public R handlerBindException(BindException e){
        log.error(e.getMessage(), e);
        return R.fail(ResultCode.FAILURE, genErrorMsg(e.getBindingResult(), e.getMessage()));
    }

    /**
     * ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public R handleValidationException(ValidationException e) {
        log.error(e.getMessage(), e);
        return R.fail(ResultCode.FAILURE, e.getCause().getMessage());
    }

    /**
     * ConstraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return R.fail(ResultCode.FAILURE, e.getMessage());
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public R handlerNoHandlerFoundException(NoHandlerFoundException e){
        log.error(e.getMessage(), e);
        return R.fail(ResultCode.NOT_FOUND, String.format("接口 [%s] 不存在", WebUtils.getRequest().getRequestURI()));
    }

    @ExceptionHandler(ServletException.class)
    public R handlerServletException(ServletException e){
        log.error(e.getMessage(), e);
        return R.fail(ResultCode.FAILURE, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        log.error(e.getMessage(), e);
        return R.fail(ResultCode.INTERNAL_SERVER_ERROR, "系统异常,请重试");
    }

    /**
     * 组装错误信息
     * @param result
     * @param errorMsg
     * @return
     */
    private String genErrorMsg (BindingResult result, String errorMsg) {
        List<ObjectError> allErrors = result.getAllErrors();
        if(CollUtil.isNotEmpty(allErrors)) {
            return allErrors.stream().map(m -> m.getDefaultMessage()).collect(Collectors.joining(", "));
        }else {
            log.error("参数校验异常：{}", errorMsg);
            return errorMsg;
        }
    }

}
