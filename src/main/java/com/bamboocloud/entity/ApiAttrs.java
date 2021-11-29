package com.bamboocloud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Apiattrs
 *
 * @author luaku
 * @date 2021/10/27
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiAttrs implements Serializable {
    String apiId ;
    String apiVersion;
    String sysId ;
    String partnerId;
    String appToken ;
    String serverUrl;
    String timeout ;
    String appSubId ;
}
