package com.bamboocloud.service;

/**
 * @author leojack
 * @message 短信发送的service类
 */
public interface SmsService {

    boolean sendVerifyCode(String mobile, String moduleCode, String moduleParam);

}
