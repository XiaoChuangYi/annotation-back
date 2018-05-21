package com.microservice.exception;

import com.microservice.result.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by cjl on 2018/5/8.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //参数校验通不过
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultVO handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String tips = "参数不合法";
        if (errors.size() > 0) {
            tips = errors.get(0).getDefaultMessage();
        }
        LOG.error("handleMethodArgumentNotValidException:" + e.getMessage(), e);
        return ResultVO.error(tips);
    }

    //所有异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultVO handleException(Exception e, HttpServletRequest request) {
        LOG.error("handleException[{} -> {}]", request.getRequestURI(), e.getMessage(), e);
        return ResultVO.error("系统开小差了...");
    }

}
