package org.imooc.coupon.service;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * <h1>优惠卷模板基础服务测试</h1>
 *
 * @Author: crowsjian
 * @Date: 2020/6/14 23:00
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateBaseTest {
    @Autowired
    private ITemplateBaseService baseService;

    @Test
    public void testBuildTemplateInfo() throws Exception {
        System.out.println(JSON.toJSONString(baseService.buildTemplateInfo(1)));
        System.out.println(JSON.toJSONString(baseService.buildTemplateInfo(2)));
    }

    @Test
    public void testFindAllUsableTemplate() {
        System.out.println(JSON.toJSONString(baseService.findAllUsableTemplate()));
    }

    @Test
    public void testFindId2TemplateSDK() {
        System.out.println(JSON.toJSONString(baseService.findIds2TemplateSDK(Arrays.asList(1,2,3,11))));
    }

}
