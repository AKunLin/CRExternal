package com.bamboocloud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author leojack
 * @message 统一返回格式
 */
@Data
@AllArgsConstructor
public class ExternalResult {

    private int code;

    private boolean success; //BAM短信平台要这个字段

    private String message;

    private Object data;

    public static int SUCCESS = 200;

    public static int FAILURE = 500;

    public static int REJECTED = 401;

    public static int dataExist = 1001;

    public static int dataLack = 1002;

    public static int dataError = 1003;

    public static int dataNotExist = 1004;


    public static ExternalResult success(Object data) {
        return new ExternalResult(SUCCESS, true, "请求成功", data);
    }

    // 500 服务器内部错误
    public static ExternalResult error(String message) {
        return new ExternalResult(FAILURE, false, "服务器内部错误", message);
    }

    // 401 鉴权失败
    public static ExternalResult rejected(String message) {
        return new ExternalResult(REJECTED, false, message, null);
    }

    // 1001 数据已存在
    public static ExternalResult dataExist(String message) {
        return new ExternalResult(dataExist, false, message, null);
    }

    // 1002 数据不存在
    public static ExternalResult dataNotExist(String message) {
        return new ExternalResult(dataNotExist, false, message, null);
    }

    // 1003 数据操作失败
    public static ExternalResult dataError(String message, Object data) {
        return new ExternalResult(1003, false, message, data);
    }

    // 1004 数据关键字段缺失
    public static ExternalResult dataLack(String message, Object data) {
        return new ExternalResult(1002, false, message, data);
    }


}
