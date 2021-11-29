package com.bamboocloud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Ecsb
 *
 * @author luaku
 * @date 2021/11/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EcsbResult {
    private String RETURN_CODE;
    private String RETURN_STAMP;
    private String RETURN_DATA;
    private String RETURN_DESC;
    public static String SUCCESS = "S1A00000";
    //请求错误
    public static String FAILREQUEST = "E0M00001";
    //请求校验失败
    public static String REJECTED = "BPMP401";

    //public static String dataExist = "EBPMP400";

    //public static String dataFail = "SEPMP000";
    //内部错误-->内部处理错误
    public static String dataError = "EPMP500";
    //数据不存在
    public static String dataNotExist = "EBPMP400";

    public static EcsbResult success(String data) {
        return new EcsbResult(SUCCESS, (new SimpleDateFormat("yyyy-mm-dd HH:mm:ss:SSS")).format(new Date()).toString(), data, "请求成功::");
    }
    public static EcsbResult errorWithRequest(String data,String message) {
        return new EcsbResult(FAILREQUEST, (new SimpleDateFormat("yyyy-mm-dd HH:mm:ss:SSS")).format(new Date()).toString(), data, "请求错误::"+message);
    }
    public static EcsbResult errorWithArm(String data) {
        return new EcsbResult(REJECTED, (new SimpleDateFormat("yyyy-mm-dd HH:mm:ss:SSS")).format(new Date()).toString(), data, "认证失败::");
    }
    public static EcsbResult errorWithData(String data) {
        return new EcsbResult(dataError, (new SimpleDateFormat("yyyy-mm-dd HH:mm:ss:SSS")).format(new Date()).toString(), data, "数据不存在::");
    }
    public static EcsbResult errorWithIn(String data,String message) {
        return new EcsbResult(dataError, (new SimpleDateFormat("yyyy-mm-dd HH:mm:ss:SSS")).format(new Date()).toString(), data, "内部处理失败::"+message);
    }



}
