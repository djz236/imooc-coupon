package org.imooc.coupon.feign;

import org.imooc.coupon.exception.CouponException;
import org.imooc.coupon.vo.CommonResponse;
import org.imooc.coupon.vo.SettlementInfo;
import org.imooc.coupon.feign.hystrix.SettlementClientHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Administrator
 * @description: 优惠券 结算微服务 Feign 接口定义
 * @param:
 * @return:
 * @date: 2021/1/24 11:25
 */
@FeignClient(value = "eureka-client-coupon-settlement",
fallback = SettlementClientHystrix.class)
public interface SettlementClient {
    /**
     * 优惠券规则计算
     * @param settlement
     * @return
     * @throws CouponException
     */
    @RequestMapping(value = "/coupon-settlement/settlement/compute",method = RequestMethod.POST)
    CommonResponse<SettlementInfo> computeRule(@RequestBody SettlementInfo settlement) throws CouponException;
}
