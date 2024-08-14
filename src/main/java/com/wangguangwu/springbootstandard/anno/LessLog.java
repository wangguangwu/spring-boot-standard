package com.wangguangwu.springbootstandard.anno;

import com.wangguangwu.springbootstandard.log.LogType;

import java.lang.annotation.*;

/**
 * 自定义注解，用于控制方法级别的日志打印行为。
 * <p>
 * 通过设置 {@code type()} 属性，可以指定需要打印哪些类型的日志。
 * 如果未设置，默认不打印任何日志（{@code LogType.NONE}）。
 *
 * @author wangguangwu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LessLog {

    /**
     * 指定要打印的日志类型。
     * 默认值为 {@code LogType.NONE}，表示不打印任何日志。
     * <p>
     * 如果想打印所有日志，可以使用 {@code LogType.ALL}。
     *
     * @return 需要打印的日志类型数组
     */
    LogType[] type() default LogType.NONE;

}
