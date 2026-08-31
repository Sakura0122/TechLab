package com.sakura.techlab.exception;

import com.sakura.techlab.common.ResultCodeEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误状态码
     */
    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    public BusinessException(ResultCodeEnum resultCodeEnum, String message) {
        super(message);
        this.code = resultCodeEnum.getCode();
    }
}
