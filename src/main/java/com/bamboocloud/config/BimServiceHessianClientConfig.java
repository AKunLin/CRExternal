package com.bamboocloud.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.bamboocloud.fw.remote.hessian.HessianBasicClient;
import com.bamboocloud.fw.remote.hessian.HessianBasicConfiguration;
import com.bamboocloud.fw.remote.hessian.HessianClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author leojack
 * @message bim的hessian接口,与我们iam交互的配置
 */
@Configuration
@Slf4j
public class BimServiceHessianClientConfig {
    @NacosValue(value = "${app.bim-server.hessian.service.baseurl:null}", autoRefreshed = true)
    private String baseUrl;
    @NacosValue(value ="${app.bim-server.hessian.service.username:null}", autoRefreshed = true)
    private String username;
    @NacosValue(value ="${app.bim-server.hessian.service.password:null}", autoRefreshed = true)
    private String password;
    @NacosValue(value ="${app.bim-server.hessian.service.pool.size:null}", autoRefreshed = true)
    private int poolSize;
    @NacosValue(value ="${app.bim-server.hessian.service.connect.timeout:null}", autoRefreshed = true)
    private int connectTimeout;
    @NacosValue(value ="${app.bim-server.hessian.service.read.timeout:null}", autoRefreshed = true)
    private int readTimeout;

    @Bean
    public HessianClient bimServiceHessianClient() {
        HessianBasicConfiguration configuration = new HessianBasicConfiguration(baseUrl, false, username, password);
        HessianClient client = new HessianBasicClient(configuration);
        return client;

    }
}
