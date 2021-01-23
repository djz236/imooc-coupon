package com.imooc.coupon.dao;

import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @description: Coupon dao 接口定义
 * @param:
 * @return:
 * @author Administrator
 * @date: 2021/1/22 22:28
 */
public interface CouponDao extends JpaRepository<Coupon,Integer> {
    /**
     * 根据 userId + 状态寻找优惠卷记录
     * @param userId
     * @param status
     * @return
     */
    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);
}
