package com.example.mybatis.muldatasource.service;

import com.example.mybatis.muldatasource.dao.SeedMapper;
import com.example.mybatis.muldatasource.dynamicds.TargetDataSource;
import com.example.mybatis.muldatasource.model.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by lixiaoming on 2018/7/26.
 */
@Component
public class SeedService {
    @Autowired
    private SeedMapper seedMapper;

    public Seed selectById(int id) {
        return seedMapper.selectByPrimaryKey(id);
    }

    @TargetDataSource(name = "test2")
    public Seed selectById2(int id) {
        return seedMapper.selectByPrimaryKey(id);
    }
}
