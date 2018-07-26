package com.example.mybatis.muldatasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @ClassName DynamicDataSourceAspect
 * @Description 切换数据源Advice
 */
@Aspect
@Order(-1)//保证该AOP在@Transactional之前执行
@Component
public class DynamicDataSourceAspect {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    @Before("@annotation(Source)")
    public void changeDataSource(JoinPoint point, TargetDataSource Source) throws Throwable {
        String dsId = Source.name();
        if (!DynamicDataSourceContextHolder.containsDataSource(dsId)) {
            logger.error("数据源[{}]不存在，使用默认数据源 > {}", Source.name(), point.getSignature());
        } else {
            logger.debug("Use DataSource : {} > {}", Source.name(), point.getSignature());
            DynamicDataSourceContextHolder.setDataSourceType(Source.name());
        }
    }

    @After("@annotation(Source)")
    public void restoreDataSource(JoinPoint point, TargetDataSource Source) {
        logger.debug("Revert DataSource : {} > {}", Source.name(), point.getSignature());
        //方法执行完毕之后，销毁当前数据源信息，进行垃圾回收。
        DynamicDataSourceContextHolder.clearDataSourceType();
    }
}