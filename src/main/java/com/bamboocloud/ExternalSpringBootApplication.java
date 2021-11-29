package com.bamboocloud;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

/**
 * 启动
 *
 * @author luaku
 * @date 2021/10/25
 */

@SpringBootApplication
@NacosPropertySource(dataId = "external.properties", autoRefreshed = true)
public class ExternalSpringBootApplication  extends SpringBootServletInitializer {


    public static void main(String[] args) throws Exception{
        SpringApplication.run(ExternalSpringBootApplication.class);
    }

}
