package com.wangguangwu.springbootstandard.response;

import com.wangguangwu.springbootstandard.enums.ResponseEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应类型，用于封装API响应数据
 *
 * @param <T> 响应数据的类型
 * @author wangguangwu
 */
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Response<T> {

    /**
     * 响应状态码
     */
    int code;

    /**
     * 响应信息
     */
    String message;

    /**
     * 响应数据
     */
    T data;

    /**
     * 构造成功的响应
     *
     * @param data 响应数据
     * @param <T>  响应数据的类型
     * @return 包含成功状态码和消息的响应对象
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 构造错误的响应
     *
     * @param code    自定义错误码
     * @param message 错误信息
     * @param <T>     响应数据的类型
     * @return 包含错误状态码和消息的响应对象
     */
    public static <T> Response<T> error(int code, String message) {
        return new Response<>(code, message, null);
    }

    /**
     * 构造错误的响应，附带响应数据
     *
     * @param resultCodeEnum 错误码枚举
     * @param data           附带的响应数据
     * @param <T>            响应数据的类型
     * @return 包含错误状态码、消息和数据的响应对象
     */
    public static <T> Response<T> error(ResponseEnum resultCodeEnum, T data) {
        return new Response<>(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), data);
    }
}
