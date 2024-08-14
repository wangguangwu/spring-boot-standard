package com.wangguangwu.springbootstandard.log;

/**
 * 日志类型枚举。
 * 用于指定在请求处理过程中，应该记录哪些类型的日志。
 *
 * @author wangguangwu
 */
public enum LogType {

    /**
     * 记录请求的URL。
     */
    URL,

    /**
     * 记录请求的参数。
     */
    REQUEST,

    /**
     * 记录响应结果。
     */
    RESPONSE,

    /**
     * 记录所有类型的日志（URL、请求参数和响应结果）。
     */
    ALL,

    /**
     * 不记录任何日志。
     */
    NONE

}