package org.imooc.coupon.feign.hystrix;

import org.imooc.coupon.vo.CommonResponse;
import org.imooc.coupon.vo.CouponTemplateSDK;
import org.imooc.coupon.feign.TemplateClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Administrator
 * @description: 优惠卷 Feign  接口的熔断降级策略
 * @param:
 * @return:
 * @date: 2021/1/24 11:43
 */
@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {
    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {
        log.error("[eureka-client-coupon-template] findAllUsableTemplate request error");
        return new CommonResponse<>(-1,
                "[eureka-client-coupon-template] findAllUsableTemplate request error",
                Collections.emptyList());
    }

    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-coupon-template] findIds2TemplateSDK request error");
        return new CommonResponse<>(-1,
                "[eureka-client-coupon-template] findAllUsableTemplate request error",
                new HashMap<>());
    }
}
