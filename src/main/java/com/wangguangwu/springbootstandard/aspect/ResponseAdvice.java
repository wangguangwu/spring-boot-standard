package com.wangguangwu.springbootstandard.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangguangwu.springbootstandard.response.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.JsonParseException;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 定义一个响应建议，用于拦截响应体并添加成功状态码。
 * 如果原始响应体为null，则返回带有成功状态码的响应；
 * 如果响应体尚未封装为Result对象，则将其封装为Result对象。
 *
 * @author wangguangwu
 */
@RequiredArgsConstructor
@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    // 用于处理 JSON 序列化的 ObjectMapper 实例
    private final ObjectMapper objectMapper;

    /**
     * 确定是否要对给定的响应执行beforeBodyWrite操作
     *
     * @param returnType    方法的返回类型
     * @param converterType 使用的HttpMessageConverter类型
     * @return 是否支持拦截响应
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        // 这里返回true，表示所有响应都将执行beforeBodyWrite操作
        return true;
    }

    /**
     * 在响应体写入前执行操作
     *
     * @param body                  原始地响应体
     * @param returnType            方法的返回类型
     * @param selectedContentType   响应的内容类型
     * @param selectedConverterType 使用的HttpMessageConverter类型
     * @param request               当前的HTTP请求
     * @param response              当前的HTTP响应
     * @return 修改后的响应体
     */
    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        // 如果响应体为null，则返回带有成功状态码的默认响应
        if (body == null) {
            return Response.success(null);
        }

        // 如果响应体已经是Response对象，则直接返回，不做修改
        if (body instanceof Response) {
            return body;
        }

        // 如果响应体是String类型，则需要手动处理字符串的包装
        if (body instanceof String) {
            try {
                // 使用ObjectMapper将Response对象转换为JSON字符串
                return objectMapper.writeValueAsString(Response.success(body));
            } catch (JsonProcessingException e) {
                // 捕获JSON处理异常并重新抛出为JsonParseException
                throw new JsonParseException(e);
            }
        }

        // 对于其他类型的响应体，封装为Response对象返回
        return Response.success(body);
    }
}
