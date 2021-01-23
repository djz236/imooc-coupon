package com.imooc.coupon.dao;

import com.imooc.coupon.entity.CouponTemplate;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Administrator
 * @description: dao 接口定义
 * @param:
 * @return:
 * @date: 2021/1/18 17:52
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate, Integer> {
    /**
     * @description: 根据模板的名称查询模板
     * @param: [name]
     * @return: com.imooc.coupon.entity.CouponTemplate
     * @author Administrator
     * @date: 2021/1/18 17:55
     */
    CouponTemplate findByName(String name);

    /**
     * @description: 根据 Available和Expired 标记查找模板记录
     * @param: [avaliable, expired]
     * @return: java.util.List<com.imooc.coupon.entity.CouponTemplate>
     * @author Administrator
     * @date: 2021/1/18 17:59
     */
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean avaliable, Boolean expired);

    /**
     * @description: 根据 Expired 标记查找模板记录
     * @param: [expired]
     * @return: java.util.List<com.imooc.coupon.entity.CouponTemplate>
     * @author Administrator
     * @date: 2021/1/18 18:01
     */
    List<CouponTemplate> findAllByExpired(Boolean expired);

}
