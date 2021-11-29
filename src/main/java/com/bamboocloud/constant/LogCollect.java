package com.bamboocloud.constant;

import com.bamboocloud.enmu.LogType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author leojack
 * @message 用于过程日志输出
 */
@Target({ElementType.METHOD})// 定义了注解声明在那些元素上，当前用在方法名上
@Retention(RetentionPolicy.RUNTIME)// 运行时有效
public @interface LogCollect {
    LogType[] value();
}
