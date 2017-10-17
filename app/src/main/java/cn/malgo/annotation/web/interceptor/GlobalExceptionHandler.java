package cn.malgo.annotation.web.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.malgo.annotation.web.result.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import cn.malgo.annotation.common.util.exception.BaseRuntimeException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true)); // true:允许输入空值，false:不能为空值
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseBody
    public ResultVO handleServletRequestBindingException(ServletRequestBindingException ex,
                                                         HttpServletRequest request) {
        LOG.error("BindingException[{} => {}]", request.getRequestURI(), ex.getMessage(), ex);
        return ResultVO.error("bing error!");
    }

    //业务异常
    @ExceptionHandler(BaseRuntimeException.class)
    @ResponseBody
    public ResultVO handleServiceException(BaseRuntimeException e, HttpServletRequest request) {
        LOG.error("ServiceException[{} => {}]", request.getRequestURI(), e.getMessage());
        return ResultVO.error(e.getMessage(), e.getCode());
    }

    //http method异常
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResultVO handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,
                                                                   HttpServletRequest request) {
        LOG.error("HttpRequestMethodNotSupportedException[{} => {}]", request.getRequestURI(),
            e.getMessage());
        return ResultVO.error("请求方法不支持!");
    }

    //参数校验通不过
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultVO handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder sb = new StringBuilder("");
        for (FieldError fieldError : fieldErrors) {
            sb.append(fieldError.getDefaultMessage()).append(";");
        }
        String msg = sb.toString();
        LOG.error("handleMethodArgumentNotValidException:" + e.getMessage(), e);
        return ResultVO.error(msg.substring(0, msg.length() - 1));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResultVO handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e,
                                                                HttpServletRequest request) {
        LOG.error("handleMethodArgumentTypeMismatchException:{} => {}", request.getRequestURI(),
            e.getMessage(), e);
        return ResultVO.error("参数类型不匹配!");
    }

    //所有异常
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResultVO handleBindException(BindException e, HttpServletRequest request) {
        LOG.error("handleException[{} -> {}]", request.getRequestURI(), e.getMessage(), e);
        return ResultVO.error("请求参数格式转换异常!");
    }

    //所有异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultVO handleException(Exception e, HttpServletRequest request) {
        LOG.error("handleException[{} -> {}]", request.getRequestURI(), e.getMessage(), e);
        return ResultVO.error("系统开小差了...");
    }
}
