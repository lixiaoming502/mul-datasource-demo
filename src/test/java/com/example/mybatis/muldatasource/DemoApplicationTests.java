package com.example.mybatis.muldatasource;

import com.example.mybatis.muldatasource.model.Seed;
import com.example.mybatis.muldatasource.service.SeedService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    private SeedService seedService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void test_SeedService1() {
        Seed seed = seedService.selectById(1);
        String url1 = seed.getUrl();
        Assert.assertEquals(url1, "http://www.81zw.com/yanqing/");
    }

    @Test
    public void test_SeedService2() {
        Seed seed2 = seedService.selectById2(1);
        String url2 = seed2.getUrl();
        Assert.assertEquals(url2, "https://m.xiaoshuoli.com/ph/week_1.htm");
    }

}
