package com.wangguangwu.springbootstandard.log;

import lombok.*;

/**
 * 日志选项类，用于配置在请求处理过程中应该记录哪些类型的日志。
 * <p>
 * 通过静态方法 {@code all()} 和 {@code none()} 可以方便地创建记录所有日志或不记录日志的选项。
 *
 * @author wangguangwu
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogOptions {

    /**
     * 是否记录请求的URL
     */
    private boolean url;

    /**
     * 是否记录请求参数
     */
    private boolean request;

    /**
     * 是否记录响应结果
     */
    private boolean response;

    /**
     * 创建一个记录所有日志选项的实例。
     *
     * @return 包含所有日志记录选项的 LogOptions 实例
     */
    public static LogOptions all() {
        return LogOptions.builder()
                .url(true)
                .request(true)
                .response(true)
                .build();
    }

    /**
     * 创建一个不记录任何日志选项的实例。
     *
     * @return 不包含任何日志记录选项的 LogOptions 实例
     */
    public static LogOptions none() {
        return LogOptions.builder()
                .url(false)
                .request(false)
                .response(false)
                .build();
    }
}
