package org.imooc.coupon.converter;

import org.imooc.coupon.constant.CouponStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/** 
 * @description: 优惠卷状态枚举类转换器
 * @param:  
 * @return:  
 * @author Administrator
 * @date: 2021/1/22 22:09
 */ 
@Converter
public class CouponStatusConverter implements AttributeConverter<CouponStatus,Integer> {

    @Override
    public Integer convertToDatabaseColumn(CouponStatus couponStatus) {
        return couponStatus.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer code) {
        return CouponStatus.of(code);
    }
}
