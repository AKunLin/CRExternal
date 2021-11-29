package com.bamboocloud.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.bamboocloud.utils.HttpClientTool;
import com.bamboocloud.utils.OIMDataSourceConn;
import com.bamboocloud.utils.RestTemplateUtils;
import com.bamboocloud.entity.RworkAuthResult;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description TODO
 * @author liufangwei@bamboocloud.com
 * @Date 2021年11月9日 下午6:36:37
 * @version 1.0.0
 * 
 */

@RestController
@RequestMapping("/rworkAuthService")
@Slf4j
public class RworkAuthController extends RworkAuthResult {
	@NacosValue(value = "${crc.cert.verifyUrl:null}", autoRefreshed = true)
	private String verifyUrl;
	@NacosValue(value = "${crc.ldap.irunAuthUrl:null}", autoRefreshed = true)
	private String irunAuthUrl;
	@NacosValue(value = "${crc.ldap.datasource.url:null}", autoRefreshed = true)
	private String DBCONURL;
	@NacosValue(value = "${crc.ldap.datasource.jndiname:null}", autoRefreshed = true)
	private String DBSOURCE;
	@NacosValue(value = "${crc.ldap.resName:null}", autoRefreshed = true)
	private String resName;
	@NacosValue(value = "${crc.ldap.itResName:null}", autoRefreshed = true)
	private String itResName;
	@NacosValue(value = "${crc.ldap.process_table:null}", autoRefreshed = true)
	private String process_table;
	@NacosValue(value = "${crc.ldap.process_target:null}", autoRefreshed = true)
	private String process_target;

	@RequestMapping(value="/authByPasswordOrPINCode", method = {RequestMethod.GET, RequestMethod.POST}, produces="application/json")
	public String rworkAuth(@RequestParam(value = "systemid") String systemid,
			@RequestParam(value = "password") String password, @RequestParam(value = "userName") String userName,
			@RequestParam(value = "userPass") String userPass,
			@RequestParam(value = "mobile", required = false) String mobile,
			@RequestParam(value = "asyuserind") String asyuserind,
			@RequestParam(value = "authType", required = false) String authType) {
		log.info("userName:" + userName + "   authType: " + authType);
		String loginInfo = "systemid~" + systemid + "^userName~" + userName + "^mobile~" + mobile;
		// 非空校验
		if ((StringUtils.isEmpty(userName) && StringUtils.isEmpty(mobile))
//				|| (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(mobile)) 
				|| StringUtils.isEmpty(password)
				|| StringUtils.isEmpty(userPass)
				|| StringUtils.isEmpty(systemid)
				|| StringUtils.isEmpty(asyuserind)) {
			log.error("登入[" + loginInfo + "]异常，代码：" + "E005" + " " + "必填参数未正确传递");
			return getResult("FALSE", "E005", "必填参数未正确传递", "NULL", "NULL", "NULL");
		}
		// authType = 2 的为证书验证
		if ((!"2".equalsIgnoreCase(authType))) {
			// get
			// https://ldapservice.crc.com.cn/commonauthservice_new/ws/OIDAuthService/userLogin?systemid=**&userName=**&userPass=**&mobile=**&asyuserind=**
			// 请求ldap认证接口
			
			Map<String, String> parMap = new HashMap();
			parMap.put("systemid", systemid);
			parMap.put("password", password);
			parMap.put("userName", userName);
			parMap.put("userPass", userPass);
			parMap.put("asyuserind", asyuserind);
			log.info(JSONObject.toJSONString(parMap));
			String result = HttpClientTool.doGetSSL(irunAuthUrl, parMap);
			log.info("IrunauthbyLDAP response:" + result);
			return result;
			
//			String ldapurl = irunAuthUrl + "?systemid=%s&password=%s&userName=%s&userPass=%s&asyuserind=%s";
//			log.info(ldapurl);
//			ldapurl = String.format(ldapurl, systemid, password, userName, URLEncoder.encode(userPass), asyuserind);
//			log.info(ldapurl);
//			ResponseEntity<String> responseEntity = RestTemplateUtils.get(ldapurl, String.class);
//			log.info(responseEntity.getBody());
//			return responseEntity.getBody();
		} else {
			// post https://10.200.7.128:443/sign_api/verify
			// 证书验证时，userPass存储签名信息-assist，mobile存储挑战值
			HashMap<String, Object> reqBody = new HashMap<String, Object>();
			//润工作采用SM2秘钥，需要拆分信息
			log.info("resp:" + userPass);
			String str[] = userPass.split("-");
			if(str.length!=2) {
				log.error("user key格式异常");
				Map<String, String> map = setResult("FALSE", "E999", "E999", "NULL", "NULL", "NULL");
				log.error("登录[" + loginInfo + "]异常，代码：" + (String) map.get("ERRCODE") + " "
						+ (String) map.get("ERRMSG"));
				return JSONObject.toJSONString(map); 
			}
			reqBody.put("ver", 2);
			reqBody.put("alg", "RAND");
			reqBody.put("rand", mobile);
			reqBody.put("sid", userName);
			reqBody.put("resp", str[0]);
			reqBody.put("assist", str[str.length-1]);
			reqBody.put("keytype", "SM2_LITE");
			
			log.info(JSONObject.toJSONString(reqBody));
			ResponseEntity<String> responseEntity = RestTemplateUtils.postJson(verifyUrl, reqBody, String.class);
			log.info("verify response status:" +responseEntity.getStatusCodeValue() + "   " + responseEntity.getBody());
			if (responseEntity.getStatusCodeValue() == 200) {
				int respCode = JSONObject.parseObject(responseEntity.getBody()).getInteger("code");
				if (respCode == 1) {
					OIMDataSourceConn oimDataSourceConn = new OIMDataSourceConn();
					try {
						// 查询OIM数据库获取工号等信息
						Map<String, String> queryMap = oimDataSourceConn
								.getUsrKeybyusrNam(oimDataSourceConn.getConnection(DBCONURL, DBSOURCE), userName);
						log.info(JSONObject.toJSONString(queryMap));

						if (((String) queryMap.get("RET_CODE")).equals("SUCCESS")) {
							// 查询ASYUSERPROV表是否有该用户记录，有返回响应
							log.info("登录[" + loginInfo + "]通过");
							if (oimDataSourceConn.CheckUserExistAsy(oimDataSourceConn.getConnection(DBCONURL, DBSOURCE),
									userName, itResName)) {
								Map<String, String> returnMap = setResult("SUCCESS", "S000", "资源同步成功",
										queryMap.get("USR_EMP_TYPE"), queryMap.get("USR_EMP_NO"), userName);
								log.warn(JSONObject.toJSONString(returnMap));
								return JSONObject.toJSONString(returnMap);
							}
							// 在ASYUSERPROV表中插入记录，插入成功返回响应
							Date date = new Date();
							DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = format.format(date);
							if (oimDataSourceConn.insertIntoUserInfo(
									oimDataSourceConn.getConnection(DBCONURL, DBSOURCE), userName,
									queryMap.get("USR_KEY"), resName, itResName, process_table, process_target, time)) {
								Map<String, String> returnMap = setResult("SUCCESS", "S000", "资源同步成功",
										queryMap.get("USR_EMP_TYPE"), queryMap.get("USR_EMP_NO"), userName);
								return JSONObject.toJSONString(returnMap);
							}
							Map<String, String> returnMap = setResult("FALSE", "E008", "资源同步失败，请联系管理员",
									queryMap.get("USR_EMP_TYPE"), queryMap.get("USR_EMP_NO"), userName);
							log.warn(JSONObject.toJSONString(returnMap));
							return JSONObject.toJSONString(returnMap);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						log.error("账号鉴权错误", e);
						Map<String, String> map = setResult("FALSE", "E999", "E999", "NULL", "NULL", "NULL");
						log.error("登录[" + loginInfo + "]异常，代码：" + (String) map.get("ERRCODE") + " "
								+ (String) map.get("ERRMSG"));
						return JSONObject.toJSONString(map);
					} finally {
						try {
							log.info("finally");
							if (oimDataSourceConn.conn != null || !oimDataSourceConn.conn.isClosed())
								oimDataSourceConn.conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				} else {
					return JSONObject.toJSONString(setResult("FALSE", "E2" + String.format("%03d", respCode),
							JSONObject.parseObject(responseEntity.getBody()).getString("msg"), "NULL", "NULL", "NULL"));
				}
			} else {
				log.warn(getResult("FALSE", "E200", "证书接口异常，请联系管理员", "NULL", "NULL", "NULL"));
				return getResult("FALSE", "E200", "证书接口异常，请联系管理员", "NULL", "NULL", "NULL");
			}
		}
		Map<String, String> result = setResult("FALSE", "E999", "E999", "NULL", "NULL", "NULL");
		return JSONObject.toJSONString(result);
	}
}
