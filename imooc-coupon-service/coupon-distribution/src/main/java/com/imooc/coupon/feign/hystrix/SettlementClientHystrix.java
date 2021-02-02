package com.imooc.coupon.feign.hystrix;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.SettlementInfo;
import com.imooc.coupon.feign.SettlementClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: 结算微服务熔断策略实现
 * @param:
 * @return:
 * @author Administrator
 * @date: 2021/1/24 11:50
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlement)
            throws CouponException {
        log.error("[eureka-client-coupon-settlement] computeRule request error ");
        settlement.setEmploy(false);
        settlement.setCost(-1.0);
        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-settlement] computeRule request error ",
                settlement
        );
    }
}
