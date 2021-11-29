package com.bamboocloud.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;


/**
 * @author leojack
 * @message 第二数据源Oracle的链接配置
 */
@Slf4j
@Configuration
@MapperScan(basePackages = "com.bamboocloud.oracle", sqlSessionFactoryRef = "slaveSqlSessionFactory")
public class OracleConfig {

    @Bean("slaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource getDb1DataSource(){
        log.info("初始化Oracle数据源 ========>");
        return DataSourceBuilder.create().build();
    }

    @Bean("slaveSqlSessionFactory")
    public SqlSessionFactory db1SqlSessionFactory(@Qualifier("slaveDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:com/bamboocloud/oracle/*.xml"));
        return bean.getObject();
    }

    @Bean("slaveSqlSessionTemplate")
    public SqlSessionTemplate db1SqlSessionTemplate(@Qualifier("slaveSqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
