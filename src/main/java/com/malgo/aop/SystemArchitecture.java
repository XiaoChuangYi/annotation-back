package com.malgo.aop;

import com.malgo.exception.MalgoServiceException;
import com.malgo.utils.OpLoggerUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
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
  @Pointcut("within(com.malgo.controller..*)")
  public void webLayer() {}

  /**
   * A join point is in the service layer if the method is defined in a type in the
   * com.xyz.someapp.service package or any sub-package under that.
   */
  @Pointcut("within(com.malgo.biz..*)")
  public void inBusinessLayer() {}

  @Pointcut("inBusinessLayer()")
  public void businessLayer() {}

  /**
   * A business service is the execution of any method defined on a service interface. This
   * definition assumes that interfaces are placed in the "service" package, and that implementation
   * types are in sub-packages.
   */
  @Pointcut(
      "execution(* com.malgo.service.*.*(..)) && !execution(* com.malgo.service.UserAccountService.*(..))&& !execution(* com.malgo.service.feigns.*.*(..))")
  public void serviceLayer() {}

  @Before("businessLayer()||serviceLayer()")
  public void beforeMethod(JoinPoint joinPoint) {
    String className = joinPoint.getTarget().getClass().getName();
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Object[] args = joinPoint.getArgs();
    if (args.length == 3) { // businessLayer
      String[] methodArr = className.split("\\.");
      if (!methodArr[methodArr.length - 1].startsWith("List")
          && !methodArr[methodArr.length - 1].startsWith("Get")) {
        log.info(
            "类名：{}；方法名：{}；用户ID：{}；角色ID：{}；请求参数：{}；",
            className,
            methodSignature.getName(),
            Integer.valueOf(args[1].toString()),
            Integer.valueOf(args[2].toString()),
            args);
      }
    } else {
      if (!methodSignature.getName().startsWith("list")) {
        log.info("类名：{}；方法名：{}；请求参数：{}；", className, methodSignature.getName(), args);
      }
    }
  }

  @AfterReturning(value = "businessLayer()||serviceLayer()", returning = "retValue")
  public void afterReturnMethod(JoinPoint joinPoint, Object retValue) {
    String className = joinPoint.getTarget().getClass().getName();
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Object[] args = joinPoint.getArgs();
    if (args.length == 3) { // businessLayer
      String[] methodArr = className.split("\\.");
      if (retValue == null) {
        OpLoggerUtil.info(
            Integer.valueOf(args[1].toString()),
            Integer.valueOf(args[2].toString()),
            methodArr[methodArr.length - 1].replace("Biz", ""),
            "无对应id记录");
      } else {
        OpLoggerUtil.info(
            Integer.valueOf(args[1].toString()),
            Integer.valueOf(args[2].toString()),
            methodArr[methodArr.length - 1].replace("Biz", ""),
            "success");
      }
      if (!methodArr[methodArr.length - 1].startsWith("List")
          && !methodArr[methodArr.length - 1].startsWith("Get")) {
        log.info(
            "类名：{}；方法名：{}；用户ID：{}；角色ID：{}；返回结果：{}；",
            className,
            methodSignature.getName(),
            Integer.valueOf(args[1].toString()),
            Integer.valueOf(args[2].toString()),
            retValue);
      }
    } else {
      if (!methodSignature.getName().startsWith("list")) {
        log.info("类名：{}；方法名：{}；返回结果：{}；", className, methodSignature.getName(), retValue);
      }
    }
  }

  @AfterThrowing(value = "businessLayer()", throwing = "ex")
  public void afterThrowMethod(JoinPoint joinPoint, MalgoServiceException ex) {
    String className = joinPoint.getTarget().getClass().getName();
    Object[] args = joinPoint.getArgs();
    if (args.length == 3) {
      String[] methodArr = className.split("\\.");
      OpLoggerUtil.info(
          Integer.valueOf(args[1].toString()),
          Integer.valueOf(args[2].toString()),
          methodArr[methodArr.length - 1].replace("Biz", ""),
          ex.getMessage());
    }
    log.info("类名：{}；异常信息：{}；", className, ex);
  }
}