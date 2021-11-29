package com.bamboocloud.utils;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class RestTemplateUtils {

    private static  RestTemplate restTemplate;

    static {
        restTemplate = new RestTemplate();
    }

    // ----------------------------------GET-------------------------------------------------------

    /**
     * GET请求调用方式
     *
     * @param url 请求URL
     * @param responseType 返回对象类型
     * @return ResponseEntity 响应对象封装类
     */
    public static <T> ResponseEntity<T> get(String url, Class<T> responseType) {
        return restTemplate.getForEntity(url, responseType);
    }

    /**
     * GET请求调用方式
     *
     * @param url 请求URL
     * @param responseType 返回对象类型
     * @param uriVariables URL中的变量，与Map中的key对应
     * @return ResponseEntity 响应对象封装类
     */
    public static <T> ResponseEntity<T> get(String url, Class<T> responseType, Map<String, ?> uriVariables) {
        return restTemplate.getForEntity(url, responseType, uriVariables);
    }

    /**
     * 带请求头的GET请求调用方式
     *
     * @param url 请求URL
     * @param headers 请求头参数
     * @param responseType 返回对象类型
     * @param uriVariables URL中的变量，与Map中的key对应
     * @return ResponseEntity 响应对象封装类
     */
    public static <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType, Map<String, ?> uriVariables) {
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        return exchange(url, HttpMethod.GET, requestEntity, responseType, uriVariables);
    }

    // ----------------------------------POST-------------------------------------------------------

    /**
     * POST请求调用方式
     *
     * @param url 请求URL
     * @param responseType 返回对象类型
     * @return
     */
    public static <T> ResponseEntity<T> post(String url, Class<T> responseType) {
        return restTemplate.postForEntity(url, HttpEntity.EMPTY, responseType);
    }

    /**
     * POST请求调用方式
     *
     * @param url 请求URL
     * @param requestBody 请求参数体,json格式传参
     * @param responseType 返回对象类型
     * @return ResponseEntity 响应对象封装类
     */
    public static <T> ResponseEntity<T> postJson(String url, Map<String,Object> requestBody, Class<T> responseType) {
        return restTemplate.postForEntity(url, requestBody, responseType);
    }


    /**
     * POST请求调用方式
     *
     * @param url 请求URL
     * @param requestParam 请求参数,Form格式传参
     * @param responseType 返回对象类型
     */
    public static <T> ResponseEntity<T> postForm(String url,MultiValueMap<String, Object> requestParam,Class<T> responseType) {
        return restTemplate.postForEntity(url, requestParam,responseType);
    }

    /**
     * POST请求调用方式
     *
     * @param url 请求URL
     * @param requestParam 请求参数,Form格式传参
     * @param responseType 返回对象类型
     */
    public static <T> ResponseEntity<T> postForm(String url, HttpHeaders headers,MultiValueMap<String, Object> requestParam,Class<T> responseType) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestParam, headers);
        return restTemplate.postForEntity(url, requestEntity,responseType);
    }


    /**
     * 带请求头的POST请求调用方式
     *
     * @param url 请求URL
     * @param headers 请求头参数
     * @param requestBody 请求参数体,json格式传参
     * @param responseType 返回对象类型
     * @return ResponseEntity 响应对象封装类
     */
    public static <T> ResponseEntity<T> postJson(String url, HttpHeaders headers, Map<String,Object> requestBody, Class<T> responseType) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
        return post(url, requestEntity, responseType);
    }


    /**
     * 自定义请求头和请求体的POST请求调用方式
     *
     * @param url 请求URL
     * @param requestEntity 请求头和请求体封装对象
     * @param responseType 返回对象类型
     * @return ResponseEntity 响应对象封装类
     */
    public static <T> ResponseEntity<T> post(String url, HttpEntity<?> requestEntity, Class<T> responseType) {
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
    }



    // ----------------------------------通用方法-------------------------------------------------------

    /**
     * 通用调用方式
     *
     * @param url 请求URL
     * @param method 请求方法类型
     * @param requestEntity 请求头和请求体封装对象
     * @param responseType 返回对象类型
     * @param uriVariables URL中的变量，与Map中的key对应
     * @return ResponseEntity 响应对象封装类
     */
    public static <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

}
