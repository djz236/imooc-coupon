package org.imooc.coupon.service;

import com.alibaba.fastjson.JSON;
import org.imooc.coupon.exception.CouponException;
import org.imooc.coupon.constant.CouponStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>用户服务功能测试用例</h1>
 * @Author: crowsjian
 * @Date: 2020/6/22 22:56
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {
    /*fake 一个 userId*/
    private Long fakeUserId = 20001L;

    @Autowired
    private IUserService userservice;

    @Test
    public void testFindCouponByStatus() throws CouponException {
        System.out.println(
                JSON.toJSONString(
                        userservice.findCouponByStatus(
                                fakeUserId,
                                CouponStatus.USABLE.getCode()
                        )
                )
        );
        System.out.println(
                JSON.toJSONString(
                        userservice.findCouponByStatus(
                                fakeUserId,
                                CouponStatus.USED.getCode()
                        )
                )
        );
        System.out.println(
                JSON.toJSONString(
                        userservice.findCouponByStatus(
                                fakeUserId,
                                CouponStatus.EXPIRED.getCode()
                        )
                )
        );
    }

    @Test
    public void testFindAvailableTemplate() throws CouponException{
        System.out.println(
                JSON.toJSONString(
                        userservice.findAvailableTemplate(fakeUserId)
                )
        );
    }
}
