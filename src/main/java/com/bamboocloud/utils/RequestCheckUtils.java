package com.bamboocloud.utils;

import com.alibaba.fastjson.JSONObject;
import com.bamboocloud.constant.ExternalConstant;
import com.bamboocloud.exception.DECException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author leojack
 * @message 接口鉴权使用
 */
@Slf4j
public class RequestCheckUtils {


    public static String ENC(String strSrc, String key) {

        return MD5Encode(strSrc, key);
    }

    public static boolean DEC(String strSrc, String key, String sign) {
        return MD5Encode(strSrc, key).equals(sign);
    }

    public static boolean CheckRequest(HttpServletRequest req, String username, String password, Object data,Boolean ifCheckRequest) throws DECException {
        if(ifCheckRequest != null && !ifCheckRequest){
           return true;
        }
        String sign = req.getHeader("bam-external-sign");
        if (StringUtils.isEmpty(sign)) {
            throw new DECException("鉴权加密内容为空!");
        }
        String strSrc = username + password + JSONObject.toJSONString(data);
        if (!DEC((strSrc), "", sign)) {
            log.info(ExternalConstant.LOG_BASE_AUTH, strSrc, sign);
            throw new DECException("鉴权内容与预期不符!");
        }

        return true;

    }

    private static String MD5Encode(String strSrc, String key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(strSrc.getBytes("UTF-8"));
            StringBuilder result = new StringBuilder(32);
            byte[] temp = md5.digest(key.getBytes("UTF-8"));
            for (int i = 0; i < temp.length; ++i) {
                result.append(Integer.toHexString((0xFF & temp[i]) | 0xFFFFFF00).substring(6));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return "";
    }


}
