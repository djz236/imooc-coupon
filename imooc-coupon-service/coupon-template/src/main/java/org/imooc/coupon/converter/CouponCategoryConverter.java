package org.imooc.coupon.converter;

import org.imooc.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author Administrator
 * @description: 优惠券分类枚举属性转换器
 * @param:
 * @return:
 * @date: 2021/1/18 17:14
 */
@Converter
public class CouponCategoryConverter implements AttributeConverter<CouponCategory, String> {

    /**
     * @description: 将尸体属性X转换为Y存储到数据库中 插入
     * @param: [couponCategory]
     * @return: java.lang.String
     * @author Administrator
     * @date: 2021/1/18 17:15
     */
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    /**
     * @description: 将数据库中的字段Y转为为实体属性X 查询操作执行的动作
     * @param: [s]
     * @return: com.immoc.coupon.constant.CouponCategory
     * @author Administrator
     * @date: 2021/1/18 17:18
     */
    @Override
    public CouponCategory convertToEntityAttribute(String code) {
        return CouponCategory.of(code);
    }
}
