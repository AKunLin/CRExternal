package com.bamboocloud.handler;

import com.bamboocloud.entity.ExternalResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletRequest;

/**
 * @author  leojack
 * @message 统一异常捕获
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理请求对象属性不满足校验规则的异常信息
     *
     * @param request
     * @param ex
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ExternalResult exception(HttpServletRequest request, MethodArgumentNotValidException ex) {
        return ExternalResult.error("对象属性不满足校验规则");
    }


    /**
     * 参数缺失
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(value =  MissingServletRequestParameterException.class)
    public ExternalResult missingExceptionHandler(HttpServletRequest request, Exception ex) {
        return  ExternalResult.error("参数缺失,请检查对应入参");
    }

    /**
     * 参数格式错误
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(value =  HttpMessageNotReadableException.class)
    public ExternalResult MismatchedInputExceptionHandler(HttpServletRequest request, Exception ex) {
        return  ExternalResult.error("参数格式错误,请检查对应入参");
    }

    /**
     * 其他未被捕获的异常
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(value =  Exception.class)
    public ExternalResult exceptionHandler(HttpServletRequest request, Exception ex) {
        ex.printStackTrace();
        return  ExternalResult.error("未被捕获的异常,请联系对应开发人员: " + ex.getCause());
    }
}

