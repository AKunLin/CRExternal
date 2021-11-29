package com.bamboocloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.bamboocloud.constant.LogCollect;
import com.bamboocloud.enmu.LogType;
import com.bamboocloud.entity.ExternalResult;
import com.bamboocloud.exception.SMSException;
import com.bamboocloud.service.SmsService;
import com.bamboocloud.utils.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import static com.bamboocloud.constant.ExternalConstant.*;
import static com.bamboocloud.constant.SmsConstant.SMS_IDP_REJECTED;
import static com.bamboocloud.constant.SmsConstant.SMS_IDP_SUCCESS;

/**
 * @author leojack
 * @mssage 短信相关的放在这里吧
 */
@RestController
@RequestMapping("/sms")
@Slf4j
public class SmsController {

    @Autowired
    private SmsService smsService;

    //idp内置的一个校验验证码的接口
    @NacosValue(value = "${sms.idp.checkUrl:null}", autoRefreshed = true)
    private String idpCheckUrl;

    @PostMapping("/sendVerifyCode")
    @LogCollect(value = {LogType.ERROR,LogType.SUCCESS,LogType.FAILURE})
    public ExternalResult sendVerifyCode(@RequestParam String mobile,
                                         @RequestParam String moduleCode,
                                         @RequestParam String moduleParam) {

        if (!smsService.sendVerifyCode(mobile, moduleCode, moduleParam)) {
            return ExternalResult.error(null);
        } else {
            return ExternalResult.success(null);
        }


    }

    /**
     * 为了方便第三方调用，把idp的接口封装一层,没什么实质性作用
     *
     * @return
     */
    @RequestMapping(value = "/idp/checkAccess", method = {RequestMethod.GET, RequestMethod.POST})
    public ExternalResult idpCheckAccess(@RequestParam String appId,
                                         @RequestParam String userName,
                                         @RequestParam String remoteIp,
                                         @RequestParam String authnMethod,
                                         @RequestParam String checkcode
    ) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("appId", appId);
        params.add("userName", userName);
        params.add("remoteIp", remoteIp);
        params.add("authnMethod", authnMethod);
        params.add("checkcode", checkcode);
        ResponseEntity<String> responseEntity = RestTemplateUtils.postForm(idpCheckUrl, params, String.class);
        //idp返回这么莫名其妙的格式我也没办法
        String result = responseEntity.getBody();
        JSONObject resultJson = JSONObject.parseObject(result);
        if (StringUtils.isNotEmpty(resultJson.getString("message"))) {
            log.error("idp response: " + result);
            return ExternalResult.rejected(SMS_IDP_REJECTED);
        }

        return ExternalResult.success(SMS_IDP_SUCCESS);
    }

}
