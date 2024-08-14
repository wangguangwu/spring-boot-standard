package com.wangguangwu.springbootstandard.aspect;

import com.alibaba.fastjson2.JSON;
import com.wangguangwu.springbootstandard.anno.LessLog;
import com.wangguangwu.springbootstandard.log.LogOptions;
import com.wangguangwu.springbootstandard.log.LogType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * 定义一个切面用于打印日志。
 * 该切面拦截指定包路径下的控制器方法，在方法执行前后打印请求和响应的日志信息。
 * 日志打印行为可以通过注解 @LessLog 配置。
 *
 * @author wangguangwu
 */
@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class RequestLoggingAspect {

    /**
     * 注入 HttpServletRequest 用于获取请求的详细信息
     */
    private final HttpServletRequest httpServletRequest;

    /**
     * 环绕通知，拦截指定包路径下的控制器方法，打印请求和响应日志
     *
     * @param joinPoint 连接点，表示被拦截的方法
     * @return 方法的返回结果
     * @throws Throwable 如果方法执行过程中发生异常，则抛出
     */
    @Around(value = "execution(* com.wangguangwu.springbootstandard.controller..*.*(..))")
    public Object aroundBack(final ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取日志选项，判断是否打印特定日志
        LogOptions logOptions = getLogOptions(joinPoint);

        // 打印请求的 URL
        if (logOptions.isUrl()) {
            log.info("request url: {}", httpServletRequest.getRequestURL().toString());
        }

        // 打印请求参数
        if (logOptions.isRequest()) {
            log.info("request params: {}", JSON.toJSON(joinPoint.getArgs()));
        }

        // 执行目标方法
        Object result = joinPoint.proceed();

        // 打印响应结果
        if (logOptions.isResponse()) {
            log.info("response:{}", JSON.toJSON(result));
        }

        return result;
    }

    /**
     * 获取方法的日志选项，根据 @LessLog 注解确定哪些日志需要打印
     *
     * @param joinPoint 连接点，表示被拦截的方法
     * @return 日志选项，确定是否打印 URL、请求参数和响应结果
     */
    private LogOptions getLogOptions(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LessLog lessLog = method.getAnnotation(LessLog.class);

        // 如果方法未标注 @LessLog 注解，则打印所有日志
        if (lessLog == null) {
            return LogOptions.all();
        }

        // 获取注解中的日志类型
        LogType[] typeArray = lessLog.type();
        EnumSet<LogType> logTypes = EnumSet.copyOf(Arrays.asList(typeArray != null && typeArray.length > 0 ? typeArray : new LogType[]{LogType.NONE}));

        // 如果包含 NONE，则打印所有日志
        if (logTypes.contains(LogType.NONE)) {
            return LogOptions.all();
        }

        // 如果包含 ALL，则不打印任何日志
        if (logTypes.contains(LogType.ALL)) {
            return LogOptions.none();
        }

        // 根据注解中的日志类型决定是否打印对应的日志
        return new LogOptions(!logTypes.contains(LogType.URL), !logTypes.contains(LogType.REQUEST), !logTypes.contains(LogType.RESPONSE));
    }
}