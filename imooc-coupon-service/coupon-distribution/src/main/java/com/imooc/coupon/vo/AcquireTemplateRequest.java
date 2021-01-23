package com.imooc.coupon.vo;

import com.immoc.coupon.vo.CouponTemplateSDK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/** 
 * @description: <h1>获取优惠券请求对象定义 <h1/>
 * @param:  
 * @return:  
 * @author Administrator
 * @date: 2021/1/23 15:40
 */ 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcquireTemplateRequest {
    /*用户 id*/
    private Long userId;

    /*优惠卷模板信息*/
    private CouponTemplateSDK templateSDK;
}
