package com.imooc.coupon.service;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.entity.CouponTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <h1>优惠卷模板基础（view,delete...）服务定义</h1>
 * @Author: crowsjian
 * @Date: 2020/6/7 11:02
 */
public interface ITemplateBaseService {
    /**
     * <h2>根据优惠卷模板id 获取优惠卷模板信息</h2>
     * @param id 模板 id
     * @return {@link CouponTemplate}优惠卷模板实体
     * @throws CouponException
     */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /**
     *<h2>查找所有可用的优惠卷模板</h2>
     * @return {@link CouponTemplateSDK}s
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * <h2>获取模板 ids 到 CouponTemplateSDK 的映射</h2>
     * @param ids 模板ids
     * @return Map<key: 模板 id, value: CouponTemplateSDK>
     */
    Map<Integer,CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
