package com.sakura.techlab.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "响应结果实体类")
public class Result<T> {

    // 返回码
    @Schema(description = "业务状态码")
    private Integer code;

    // 返回消息
    @Schema(description = "响应消息")
    private String message;

    // 返回数据
    @Schema(description = "业务数据")
    private T data;

    // 私有化构造
    private Result() {
    }

    // 返回数据
    public static <T> Result<T> success(T body) {
        return build(body, 200, "success");
    }

    public static <T> Result<T> success() {
        return build(null, 200, "success");
    }

    public static <T> Result<T> error(int code, String message) {
        return build(null, code, message);
    }

    public static <T> Result<T> error(ResultCodeEnum resultCodeEnum) {
        return build(null, resultCodeEnum);
    }

    public static <T> Result<T> error(ResultCodeEnum resultCodeEnum, String message) {
        return build(null, resultCodeEnum.getCode(), message);
    }

    public static <T> Result<T> build(T body, Integer code, String message) {
        Result<T> result = new Result<>();
        result.setData(body);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        return build(body, resultCodeEnum.getCode(), resultCodeEnum.getMessage());
    }

}
