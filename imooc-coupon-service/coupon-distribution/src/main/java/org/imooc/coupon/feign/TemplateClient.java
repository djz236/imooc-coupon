package org.imooc.coupon.feign;

import org.imooc.coupon.vo.CommonResponse;
import org.imooc.coupon.vo.CouponTemplateSDK;
import org.imooc.coupon.feign.hystrix.TemplateClientHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @description: 优惠券模板微服务 Feign 接口定义
 * @param:
 * @return:
 * @date: 2021/1/24 11:12
 */
@FeignClient(value = "eureka-client-coupon-template",
fallback = TemplateClientHystrix.class)
public interface TemplateClient {
    /**
     * <h2>查找所有可用的优惠券模板</h2>
     *
     * @return
     */
    @RequestMapping(value = "/coupon-template/template/sdk/all",
            method = RequestMethod.GET)
    CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate();

    /**
     * 获取模板ids 到 CouponTemplateSDK 的映射
     * @param ids
     * @return
     */
    @RequestMapping(value = "/coupon-template/template/sdk/infos",
            method = RequestMethod.GET)
    CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(
          @RequestParam("ids") Collection<Integer> ids
    );
}
