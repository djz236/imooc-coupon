package com.imooc.coupon.service;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.PeriodType;
import com.imooc.coupon.constant.ProductLine;
import com.imooc.coupon.vo.TemplateRule;
import com.imooc.coupon.vo.TemplateRequest;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * @author Administrator
 * @description: 构建优惠券模板服务测试
 * @param:
 * @return:
 * @date: 2021/1/21 20:20
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BuildTemplateTest {
    @Autowired
    private IBuildTemplateService buildTemplateService;

    @Test
    public void testBuildTemplate() throws Exception {
        System.out.println(JSON.toJSONString(buildTemplateService.buildTemplate(fakeTemplateRequest())));
        //防止主线程执行结束后，创建优惠券码的子线程结束导致报错失败。
        Thread.sleep(5000);
    }

    /**
     * <h2>fake TemplateRequest</h2>
     *
     * @return
     */
    private TemplateRequest fakeTemplateRequest() {
        TemplateRequest request = new TemplateRequest();
        request.setName("优惠卷模板-" + new Date().getTime());
        request.setLogo("www.baidu.com");
        request.setDesc("这是一张优惠卷模板");
        request.setCategory(CouponCategory.MANJIAN.getCode());
        request.setProductLine(ProductLine.DAMAO.getCode());
        request.setCount(10);//fake userId
        request.setUserId(10001L);
        request.setTarget(DistributeTarget.SINGLE.getCode());
        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                PeriodType.SHIFT.getCode(), 1, DateUtils.addDays(new Date(), 60).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(5, 1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage(
                "河北省", "石家庄市",
                JSON.toJSONString(Arrays.asList("文娱", "家具"))
        ));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));
        request.setRule(rule);
        return request;
    }
}
