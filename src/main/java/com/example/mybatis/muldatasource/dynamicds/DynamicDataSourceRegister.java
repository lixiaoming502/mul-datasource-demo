package com.example.mybatis.muldatasource.dynamicds;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 动态数据源注册
 */
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceRegister.class);
    // 数据源
    private DataSource defaultDataSource;
    private Map<String, DataSource> customDataSources = new HashMap<>();

    /**
     * 创建DataSource
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public DataSource buildDataSource(Map<String, Object> dsMap) {
        String driverClassName = dsMap.get("driver-class-name").toString();
        String url = dsMap.get("url").toString();
        String username = dsMap.get("username").toString();
        String password = dsMap.get("password").toString();

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassName);
        return ds;
    }

    /**
     * 加载多数据源配置
     */
    @Override
    public void setEnvironment(Environment env) {
        initDefaultDataSource(env);
        initCustomDataSources(env);
    }

    /**
     * 初始化主数据源
     */
    public void initDefaultDataSource(Environment env) {
        // 读取主数据源,RelaxedPropertyResolver在spring2.0中已经废弃
        //RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
        String prefix = "spring.datasource.";
        Map<String, Object> dsMap = buildMap(env, prefix);

        defaultDataSource = buildDataSource(dsMap);
    }

    private Map<String, Object> buildMap(Environment env, String prefix) {
        Map<String, Object> dsMap = new HashMap<>();
        dsMap.put("type", env.getProperty(prefix + "type"));
        dsMap.put("driver-class-name", env.getProperty(prefix + "driver-class-name"));
        dsMap.put("url", env.getProperty(prefix + "url"));
        dsMap.put("username", env.getProperty(prefix + "username"));
        dsMap.put("password", env.getProperty(prefix + "password"));
        return dsMap;
    }

    /**
     * 初始化更多数据源
     */
    public void initCustomDataSources(Environment env) {
        String prefix = "custom.datasource.";
        String names = env.getProperty(prefix + "names");
        for (String name : names.split(",")) {// 多个数据源
            String namePrefix = prefix + name + ".";
            Map<String, Object> dsMap = buildMap(env, namePrefix);
            customDataSources.put(name, buildDataSource(dsMap));
        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        // 将主数据源添加到更多数据源中
        targetDataSources.put("dataSource", defaultDataSource);
        DynamicDataSourceContextHolder.dataSourceIds.add("dataSource");
        // 添加更多数据源
        targetDataSources.putAll(customDataSources);
        for (String key : customDataSources.keySet()) {
            DynamicDataSourceContextHolder.dataSourceIds.add(key);
        }
        // 创建DynamicDataSource
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();
        mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
        mpv.addPropertyValue("targetDataSources", targetDataSources);
        registry.registerBeanDefinition("dataSource", beanDefinition);

        logger.info("Dynamic DataSource Registry");
    }
}