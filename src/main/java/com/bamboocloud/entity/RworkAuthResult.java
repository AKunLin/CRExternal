package com.bamboocloud.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description TODO
 * @author liufangwei@bamboocloud.com
 * @Date 2021年11月9日 下午9:11:40
 * @version 1.0.0
 * 
 */
public class RworkAuthResult {
	  public static final String SUCCESS = "SUCCESS";
	  
	  public static final String FALSE = "FALSE";
	  
	  public static final String USERTYPE = "NULL";
	  
	  public static final String USR_EMP_NO = "NULL";
	  
	  public static final String USERLOGIN = "NULL";
	  
	  public Map<String, String> setResult(String ret, String errCode, String errMsg, String userType, String usr_emp_no, String userlogin) {
	    Map<String, String> result = new ConcurrentHashMap<String, String>();
	    result.put("RET", ret);
	    result.put("ERRCODE", errCode);
	    result.put("ERRMSG", errMsg);
	    result.put("USERTYPE", userType);
	    result.put("USR_EMP_NO", usr_emp_no);
	    result.put("USERLOGIN", userlogin);
	    return result;
	  }
	  
	  public String getResult(String ret, String errCode, String errMsg, String userType, String usr_emp_no, String userlogin) {
	    Map<String, String> result = new ConcurrentHashMap<String, String>();
	    result.put("RET", ret);
	    result.put("ERRCODE", errCode);
	    result.put("ERRMSG", errMsg);
	    result.put("USERTYPE", userType);
	    result.put("USR_EMP_NO", usr_emp_no);
	    result.put("USERLOGIN", userlogin);
	    String jsonString = JSONObject.toJSONString(result);
	    return jsonString;
	  }
	  
	  public String getResultCode(Map<String, String> result) {
	    return result.get("ERRCODE");
	  }
}
