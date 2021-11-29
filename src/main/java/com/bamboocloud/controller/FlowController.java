package com.bamboocloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.bamboocloud.constant.LogCollect;
import com.bamboocloud.enmu.LogType;
import com.bamboocloud.entity.ApiAttrs;
import com.bamboocloud.entity.EcsbResult;
import com.bamboocloud.entity.Flow;
import com.bamboocloud.im.entity.User;
import com.bamboocloud.service.AcceptService;
import com.bamboocloud.service.BimHessianService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;


/**
 * 外部申请控制层
 * 外部账号申请流程 (待改进)
 * 需要实现将外部公司导入机构
 * @author luaku
 * @date 2021/10/25
 */

@RestController
@RequestMapping("/external")
@Slf4j
public class FlowController {
    @Autowired
    private AcceptService acceptService;
    @Autowired
    BimHessianService bimHessianService;
    @NacosValue(value = "${apiattrs.apiId:null}",autoRefreshed = true)
    private String myapiId;
    @NacosValue(value = "${apiattrs.apiVersion:null}",autoRefreshed = true)
    private String myApiVersion;
    @NacosValue(value = "${apiattrs.appToken:null}",autoRefreshed = true)
    private String mytoken;
    @NacosValue(value = "${apiattrs.externalOrg:null}",autoRefreshed = true)
    private String externalOrgcode;

    /*{
    "bindId":"b1ea5d1b-23e2-45fb-87a4-f6bf29b56ae5"
    }*/

    @PostMapping(value = "/registerBimAcc",produces = "application/json;charset=UTF-8")
    @LogCollect(LogType.ALL)
    public EcsbResult registerBimAcc(@RequestBody String jsonObj) {
        System.out.println(jsonObj);

        //System.out.println(overdd);
        JSONObject json = JSON.parseObject(jsonObj);
        //获取request
        Object request = json.get("REQUEST");
        JSONObject jsonObject = JSONObject.parseObject(request.toString());
        //获取Request_data

        Object request_data = jsonObject.get("REQUEST_DATA");
        JSONObject bindobj = JSONObject.parseObject(request_data.toString());
        String bindId = bindobj.get("bindId").toString();
        String requestData;
        //校验API_ATTRS
        Object api_attrs = jsonObject.get("API_ATTRS");
        ApiAttrs apiAttrs = JSON.parseObject(api_attrs.toString(), ApiAttrs.class);

        String s = apiAttrs.toString();
        if(!myapiId.equals(apiAttrs.getApiId())){
            //log.error("myapiId校验失败");
            return EcsbResult.errorWithArm(bindId);
        }
        if(!apiAttrs.getApiVersion().equals(myApiVersion)){
            //log.error("myApiVersion校验失败");
            return EcsbResult.errorWithArm(bindId);
        }try {
            Flow flow = acceptService.findByBinId(bindId);
            if(flow == null){
                log.error("没有查询到外部账号流程信息");
                return EcsbResult.errorWithData(bindId);

            }
            /**
             * TODO 添加注释
             * 需要将用户存入bim库中待定修改
             * (代码 优化空间大)待改进
             **/
            log.info("查询出来的flow为:"+flow.toString());
                User user = new User();
                user.setFirstname(flow.getUsername());
                user.setFullname(flow.getUsername());
                user.setEmail(flow.getEmail());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                user.setCreateAt(timestamp);
                user.setMobile(flow.getTelphone());
                String externalOrg = bimHessianService.getOrgByCode(externalOrgcode).getId();
                user.setOrganizationId(externalOrg);
                user.setUsername(flow.getLdapaccout());
                user.setPassword("P@ssw0rd!");
                bimHessianService.addBimUser(user);
                return EcsbResult.success(flow.toString());
        } catch (Exception e) {
           e.printStackTrace();
           log.error(e.getMessage());
            return EcsbResult.errorWithIn(bindId,e.getMessage());
        }
    }
}
