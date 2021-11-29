package com.bamboocloud.enmu;

import lombok.AllArgsConstructor;

/**
 * @author leojack
 * @message 日志对应状态的收集
 */
@AllArgsConstructor
public enum LogType {

    WAITTING(1,"收集状态为 待定 的日志"),
    FINSIH(2,"收集状态为 完成 的日志"),
    SUCCESS(3,"收集状态为 成功 的日志"),
    FAILURE(4,"收集状态为 失败 的日志"),
    ERROR(5,"收集状态为 异常 的日志"),  //如果要收集异常类日志，在方法中不要捕获对应异常,或捕获后抛出
    ALL(6,"收集所有状态的日志");

    public Integer code;
    public String msg;


}
