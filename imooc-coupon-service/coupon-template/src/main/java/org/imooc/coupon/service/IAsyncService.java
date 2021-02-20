package org.imooc.coupon.service;

import org.imooc.coupon.entity.CouponTemplate;

/**
 * <h1>异步服务接口定义</h1>
 * @Author: crowsjian
 * @Date: 2020/6/6 0:21
 */
public interface IAsyncService {
    /**
     * <h2>根据模板异步的创建优惠卷码</h2>
     * @param template {@link CouponTemplate} 优惠卷模板实体
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
