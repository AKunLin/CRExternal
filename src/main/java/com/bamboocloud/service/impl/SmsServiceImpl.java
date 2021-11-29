package com.bamboocloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.bamboocloud.service.SmsService;
import com.bamboocloud.utils.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {


    @NacosValue(value = "${sms.openUrl:null}", autoRefreshed = true)
    private String openUrl;

    @NacosValue(value = "${sms.submiterName:null}", autoRefreshed = true)
    private String submiterName;

    @NacosValue(value = "${sms.submiter:null}", autoRefreshed = true)
    private String submiter;

    @NacosValue(value = "${sms.tenantId:null}", autoRefreshed = true)
    private String tenantId;

    @NacosValue(value = "${sms.appId:null}", autoRefreshed = true)
    private String appId;

    @NacosValue(value = "${sms.signCode:null}", autoRefreshed = true)
    private String signCode;

    @NacosValue(value = "${sms.templateCode:null}", autoRefreshed = true)
    private String templateCode;

    @NacosValue(value = "${sms.extend:null}", autoRefreshed = true)
    private String extend;

    @NacosValue(value = "${sms.crc.appId:null}", autoRefreshed = true)
    private String crcAppId;

    @NacosValue(value = "${sms.crc.apiId:null}", autoRefreshed = true)
    private String crcApiId;

    @NacosValue(value = "${sms.crc.token:null}", autoRefreshed = true)
    private String crcToken;

    @NacosValue(value = "${sms.crc.secret:null}", autoRefreshed = true)
    private String crcSecret;

    @NacosValue(value = "${sms.crc.stage:null}", autoRefreshed = true)
    private String crcStage;

    /**
     * 调用华润这边短信服务器的关键代码
     *
     * @param mobile
     * @param moduleCode
     * @param moduleParam
     * @return
     */
    @Override
    public boolean sendVerifyCode(String mobile, String moduleCode, String moduleParam) {

        HashMap<String, Object> param = new HashMap<>();
        param.put("submiterName", submiterName);
        param.put("submiter", submiter);
        param.put("tenantId", tenantId);
        param.put("phoneNumber", mobile);
        param.put("appId", appId);
        param.put("signCode", signCode);
        param.put("templateCode", templateCode);
        param.put("msg_tplparams2", JSONObject.parseObject(moduleParam));
        param.put("extend", extend);

        HttpHeaders headers = new HttpHeaders();
        headers.add("s-crc-app-id", crcAppId);
        headers.add("s-crc-api-id", crcApiId);
        headers.add("s-crc-token", crcToken);
        headers.add("s-crc-secret", crcSecret);
        headers.add("s-crc-stage", crcStage);
        headers.add("Content-Type", "application/json;charset=utf-8");
        ResponseEntity<String> response = RestTemplateUtils.postJson(openUrl, headers, param, String.class);
        if (response.getStatusCodeValue() == 200) {
            String responseBody = response.getBody();
            JSONObject resultJson = JSONObject.parseObject(responseBody);
            if (!"200".equals(resultJson.getString("code"))) {
                return false;
            }
            return true;
        } else {
            return false;
        }

    }
}
