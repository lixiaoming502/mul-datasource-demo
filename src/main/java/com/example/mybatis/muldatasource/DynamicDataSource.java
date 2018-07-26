package com.example.mybatis.muldatasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


public class DynamicDataSource extends AbstractRoutingDataSource {
    //设置当前的数据源，在路由类中使用getDataSourceType进行获取，
//  交给AbstractRoutingDataSource进行注入使用。
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceType();
    }
}