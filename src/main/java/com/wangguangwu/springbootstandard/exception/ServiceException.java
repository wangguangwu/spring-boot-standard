package com.wangguangwu.springbootstandard.exception;

import com.wangguangwu.springbootstandard.enums.ResponseEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义业务异常。
 *
 * @author wangguangwu
 */
@SuppressWarnings("unused")
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends RuntimeException {

    protected final Integer data;
    protected final String message;

    public ServiceException() {
        this.data = ResponseEnum.SERVICE_UNKNOWN.getCode();
        this.message = ResponseEnum.SERVICE_UNKNOWN.getMessage();
    }

    public ServiceException(String message) {
        this.data = ResponseEnum.SERVICE_UNKNOWN.getCode();
        this.message = message;
    }
}
