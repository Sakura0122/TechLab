package com.sakura.techlab.common;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    PARAMS_ERROR(400, "请求参数错误"),
    NOT_LOGIN_ERROR(401, "未登录"),
    NO_AUTH_ERROR(403, "无权限"),
    NOT_FOUND_ERROR(404, "请求数据不存在"),
    SYSTEM_ERROR(500, "系统内部异常"),
    OPERATION_ERROR(500, "操作失败"),
    UPDATE_ERROR(500, "更新失败")
    ;

    /**
     * 业务状态码
     */
    private final Integer code;
    /**
     * 响应消息
     */
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
