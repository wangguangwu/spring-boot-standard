package com.wangguangwu.springbootstandard.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应结果枚举，用于定义通用的响应状态和信息
 * <p>
 * {@link  com.wangguangwu.springbootstandard.response.Response}
 *
 * @author wangguangwu
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ResponseEnum {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),

    /**
     * 失败
     */
    FAIL(-1, "失败"),

    /**
     * 业务层异常
     */
    SERVICE_UNKNOWN(1000, "业务层异常"),

    /**
     * 系统未知异常
     */
    SYSTEM_UNKNOWN(1001, "系统未知异常");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态信息
     */
    private final String message;

}