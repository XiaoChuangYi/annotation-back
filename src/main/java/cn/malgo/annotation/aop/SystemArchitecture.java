package cn.malgo.annotation.aop;

import cn.malgo.annotation.request.brat.BaseAnnotationRequest;
import cn.malgo.annotation.utils.OpLoggerUtil;
import cn.malgo.service.exception.MalgoServiceException;
import cn.malgo.service.model.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/9. */
@Aspect
@Component
@Slf4j
public class SystemArchitecture {

  /**
   * A join point is in the web layer if the method is defined in a type in the com.xyz.someapp.web
   * package or any sub-package under that.
   */
  @Pointcut("within(cn.malgo.annotation.controller..*)")
  public void webLayer() {}

  /**
   * A join point is in the service layer if the method is defined in a type in the
   * com.xyz.someapp.service package or any sub-package under that.
   */
  @Pointcut(
      "within(cn.malgo.annotation.biz..*) && !within(cn.malgo.annotation.biz.doc.ImportDocBiz)")
  public void inBusinessLayer() {}

  @Pointcut("inBusinessLayer()")
  public void businessLayer() {}

  /**
   * A business service is the execution of any method defined on a service interface. This
   * definition assumes that interfaces are placed in the "service" package, and that implementation
   * types are in sub-packages.
   */
  @Pointcut(
      "execution(* cn.malgo.annotation.service.*.*(..))"
          + " && !execution(* cn.malgo.annotation.service.AnnotationFactory.*(..))"
          + " && !execution(* cn.malgo.annotation.service.feigns.*.*(..))")
  public void serviceLayer() {}

  private boolean isReadMethod(final String className) {
    return className.startsWith("List")
        || className.startsWith("Get")
        || className.startsWith("Search")
        || className.startsWith("Import")
        || className.startsWith("Find");
  }

  @Before("businessLayer()")
  public void beforeMethod(JoinPoint joinPoint) {
    String className = joinPoint.getTarget().getClass().getName();
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Object[] args = joinPoint.getArgs();
    if (args.length == 3) { // businessLayer
      String[] methodArr = className.split("\\.");

      if (!isReadMethod(methodArr[methodArr.length - 1])) {
        log.info(
            "类名：{}；方法名：{}；用户ID：{}；角色ID：{}；请求参数：{}；",
            className,
            methodSignature.getName(),
            Integer.valueOf(args[1].toString()),
            Integer.valueOf(args[2].toString()),
            args);
      }
    } else {
      if (!isReadMethod(StringUtils.substringAfterLast(className, "."))) {
        log.info("类名：{}；方法名：{}；请求参数：{}；", className, methodSignature.getName(), args);
      }
    }
  }

  @AfterReturning(value = "businessLayer()", returning = "retValue")
  public void afterReturnMethod(JoinPoint joinPoint, Object retValue) {
    String className = joinPoint.getTarget().getClass().getName();
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Object[] args = joinPoint.getArgs();
    if (args.length == 2) { // businessLayer
      long anId = 0;
      if (args[0] instanceof BaseAnnotationRequest) {
        anId = ((BaseAnnotationRequest) args[0]).getId();
      }
      String[] methodArr = className.split("\\.");
      final long userId =
          args[1] != null && (args[1] instanceof UserDetails) ? ((UserDetails) args[1]).getId() : 0;
      OpLoggerUtil.info(
          userId,
          methodArr[methodArr.length - 1].replace("Biz", ""),
          retValue == null ? "" : "success",
          anId);

      if (!isReadMethod(methodArr[methodArr.length - 1])) {
        log.info(
            "类名：{}；方法名：{}；用户ID：{}；返回结果：{}；",
            className,
            methodSignature.getName(),
            userId,
            retValue);
      }
    } else {
      if (!isReadMethod(StringUtils.substringAfterLast(className, "."))) {
        log.info("类名：{}；方法名：{}；返回结果：{}；", className, methodSignature.getName(), retValue);
      }
    }
  }

  @AfterThrowing(value = "businessLayer()", throwing = "ex")
  public void afterThrowMethod(JoinPoint joinPoint, MalgoServiceException ex) {
    String className = joinPoint.getTarget().getClass().getName();
    Object[] args = joinPoint.getArgs();
    if (args.length == 2) {
      long anId = 0;
      if (args[0] instanceof BaseAnnotationRequest) {
        anId = ((BaseAnnotationRequest) args[0]).getId();
      }
      String[] methodArr = className.split("\\.");
      OpLoggerUtil.info(
          args[1] != null && (args[1] instanceof UserDetails) ? ((UserDetails) args[1]).getId() : 0,
          methodArr[methodArr.length - 1].replace("Biz", ""),
          ex.getMessage(),
          anId);
    }
    log.info("类名：{}；异常信息：{}；", className, ex);
  }
}
