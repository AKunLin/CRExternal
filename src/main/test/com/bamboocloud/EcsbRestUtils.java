package com.bamboocloud;


import java.util.Properties;



import com.crc.openapi.sdk.client.SysClient;
import com.crc.openapi.sdk.entity.ApiCommonParameter;
import com.crc.openapi.sdk.entity.Result;
import com.crc.openapi.sdk.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import static com.crc.openapi.sdk.common.CommonEnum.ChainOrderEnum.SIGN_ONLY;
import static com.crc.openapi.sdk.common.CommonEnum.GatewayTypeEnum.*;
import static com.crc.openapi.sdk.common.CommonEnum.HttpMethodEnum.GET;
import static com.crc.openapi.sdk.common.CommonEnum.ServerTypeEnum.RESTFUL;
import static com.crc.openapi.sdk.common.CommonEnum.SignMethodEnum.NO_SIGN;

/**
 * EcsbRest工具类
 *
 * @author luaku
 * @date 2021/10/26
 */
public class EcsbRestUtils {
    public static Result callSSDPRest(String vo) {
        String apiId = "snowbeer.fssc.emsppm.bpmCallBack";
        String apiVersion = "1.0";
        String sysId = "12000003";
        String partnerId = "12000000";
        String appToken = "8a16a05947414ddbbde3fd0fe443939a";
        String serverUrl = "https://ecsb-uat.crcloud.com:8443/ecsb/gw";
        String timeout = "30000";
        String appSubId = "1200000301WV";
        // 正常请求配置
        Properties baseProps = new Properties();
        baseProps.setProperty("http.ip", serverUrl);
        baseProps.setProperty("gateway",GatewayTypeEnum.SOA.name());
        baseProps.setProperty("model", ServerTypeEnum.REST.name());
        baseProps.setProperty("method", HttpMethodEnum.POST.name());
        baseProps.setProperty("order", ChainOrderEnum.DES_FIRST.name());
        SysClient client = new SysClient(baseProps);
        // 目标api参数设定
        ApiCommonParameter parameter = new ApiCommonParameter();
        parameter.setApiID(apiId);
        parameter.setApiVersion(apiVersion);
        parameter.setAppSubId(appSubId);
        parameter.setSysID(sysId);
        parameter.setPartnerID(partnerId);
        parameter.setAppToken(appToken);
        parameter.setSignMethod(SignMethodEnum.NO_SIGN);
        parameter.setTimeStamp(DateUtil.nowDateFormatDefault());
        // 设置业务请求参数
        parameter.setRequestDate(vo);
        // SDK客户端超时时间，默认30000
        if (StringUtils.isBlank(timeout)) {
            parameter.setTimeout(30000);
        } else {
            parameter.setTimeout(Integer.parseInt(timeout));
        }
        // String dd = MD5.toDigestUTF8(vo);
        // System.out.println("\n开始SSDP调用Rest接口:["+appId+"]--->"+dd+"--->"+DateUtil.nowDateFormatDefault());
        Result result = client.post(parameter);
        // System.out.println("\n完成SSDP调用Rest接口:["+appId+"]--->"+dd+"--->"+DateUtil.nowDateFormatDefault()+"--->"+result.getReturnHeader().get("ssdp_sn"));
        // System.out.println("\n回调Rest接口:["+ appId
        // +"],--->"+dd+"--->的SSDP请求结果为：\n"+result);
        return result;
    }
}
