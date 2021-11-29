package com.bamboocloud;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 返回响应报文
 *
 * @author luaku
 * @date 2021/10/26
 */
public class ResultResp {

    /**
     * 错误返回
     * @author luaku
     * @date 2021/11/1
     * @param errcode,data,message
     * @return string
     **/
    public static JSON error(String errcode,String data,String message){
        HashMap<String,Object> hamap = new LinkedHashMap<>();
        //throw new RuntimeException("BindId isEmpty");
        long l = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss:SSS");
        String format1 = format.format(l);
        hamap.put("RETURN_CODE",errcode);
        hamap.put("RETURN_STAMP",format1);
        hamap.put("RETURN_DATA",data);
        hamap.put("RETURN_DESC",message);
        JSONObject json = new JSONObject(true);
        json.put("RESPONSE",hamap);
        //json.put("RETURN_DESC",message);
        return json;
    }
    /**
     * 正确
     * @author luaku
     * @date 2021/11/1
     * @param succode,data,message
     * @return string
     **/
    public static JSON ok(String succode,String data,String message){
        HashMap<String,Object> hamap = new LinkedHashMap<>();
        //throw new RuntimeException("BindId isEmpty");
        long l = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss:SSS");
        String format1 = format.format(l);
        hamap.put("RETURN_CODE",succode);
        hamap.put("RETURN_STAMP",format1);
        hamap.put("RETURN_DATA",data);
        hamap.put("RETURN_DESC",message);
        JSONObject json = new JSONObject(true);
        json.put("RESPONSE",hamap);
        //json.put("RETURN_DESC",message);
        return json;
    }

}
