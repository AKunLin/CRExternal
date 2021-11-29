package com.bamboocloud.config;


import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author leojack
 * @message nacos连接config
 */
@Configuration
public class NacosRigisterConfig {

    @Value("${server.port}")
    private int port;

    @Value("${spring.application.name}")
    private String appName;

    @NacosInjected
    private NamingService namingService;

    @PostConstruct
    public void registerInstance() throws NacosException, UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        namingService.registerInstance(appName,addr.getHostAddress(),port,"DEFAULT");
    }

}
