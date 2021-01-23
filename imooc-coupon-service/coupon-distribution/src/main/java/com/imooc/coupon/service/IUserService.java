package com.imooc.coupon.service;

import com.immoc.coupon.exception.CouponException;
import com.immoc.coupon.vo.CouponTemplateSDK;
import com.immoc.coupon.vo.SettlementInfo;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.vo.AcquireTemplateRequest;

import java.util.List;

/**
 * <h1>用户服务相关的接口定义</h1>
 * 1.用户三类状态优惠卷信息展示服务
 * 2.查看用户当前 可以领取的优惠卷模板 - coupon-template 微服务配合实现
 * 3.用户领取优惠卷服务
 * 4.用户消费优惠卷服务 - coupon-settlement 微服务配合实现
 *
 * @Author: crowsjian
 * @Date: 2020/6/16 22:01
 */
public interface IUserService {
    /**
     * <h2>根据用户id和状态查询优惠卷记录</h2>
     *
     * @param userId 用户 id
     * @param status 优惠卷状态
     * @return {@link Coupon}s
     * @throws CouponException
     */
    List<Coupon> findCouponByStatus(Long userId,
                                    Integer status) throws CouponException;

    /**
     * <h2>根据用户id查到当前可以领取的优惠卷模板</h2>
     *
     * @param userId 用户 id
     * @return {@link CouponTemplateSDK}s
     * @throws CouponException
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;
    /**
     * <h2>用户领取优惠卷</h2>
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     * @throws CouponException
     */
    Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;

    /**
     * <h2>结算（核销） 优惠卷</h2>
     * @param info @link SettlementInfo}
     * @return {@link SettlementInfo}
     * @throws CouponException
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;
}
