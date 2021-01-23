package com.imooc.coupon.service;

import com.immoc.coupon.exception.CouponException;
import com.imooc.coupon.entity.Coupon;

import java.util.List;

/** 
 * @description:   
 * <h1>Redis 相关的操作服务接口定义</h1>
 * 1. 用户的三个状态优惠卷 Cache 相关操作
 * 2. 优惠卷模板生成的优惠卷码 Cache  操作
 * @param:  
 * @return:  
 * @author Administrator
 * @date: 2021/1/22 22:34
 */ 
public interface IRedisService {
    /** 
     * @description:  根据userId 和 状态 找到缓存的优惠卷列表数据
     * @param: [userId, status] userId 用户 id ,status 优惠卷状态 {@link com.imooc.coupon.constant.CouponStatus}
     * @return: java.util.List<com.imooc.coupon.entity.Coupon>
     *      {@link Coupon}s，注意，可能会返回 null,代表没有过记录
     * @author Administrator
     * @date: 2021/1/22 22:36
     */ 
    List<Coupon> getCachedCoupons(Long userId,Integer status);

    /**
     * 保存空的优惠卷列表到缓存中  考虑缓存穿透的问题
     * @param userId
     * @param status
     */
    void saveEmptyCouponListToCache(Long userId,List<Integer> status);
    /**
     * <h2>尝试从 Cache 中获取一个优惠卷码</h2>
     * @param templateId 优惠卷模板主键
     * @return 优惠卷码
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);
    /**
     * <h2>将优惠卷保存到 cache 中</h2>
     * @param userId 用户 id
     * @param coupons {@link Coupon}s
     * @param status 优惠卷状态
     * @return 保存成功的个数
     * @throws CouponException
     */
    Integer addCouponToCache(Long userId,List<Coupon> coupons,Integer status) throws CouponException;
}
