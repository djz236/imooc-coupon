package com.imooc.coupon.service;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.vo.TemplateRequest;

/** 
 * @description: 构建优惠卷模板接口定义 
 * @param:  
 * @return:  
 * @author Administrator
 * @date: 2021/1/18 20:54
 */ 
public interface IBuildTemplateService {
    /**
     * <h2>创建优惠卷模板</h2>
     * @param templateRequest {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠卷模板实体
     * @throws CouponException
     */
    CouponTemplate buildTemplate(TemplateRequest templateRequest) throws CouponException;
}
