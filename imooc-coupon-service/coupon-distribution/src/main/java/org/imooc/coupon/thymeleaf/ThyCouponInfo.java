package org.imooc.coupon.thymeleaf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.imooc.coupon.entity.Coupon;

import java.text.SimpleDateFormat;

/**
 * <h1>用户优惠卷信息</h1>
 * @Author: crowsjian
 * @Date: 2020/7/5 9:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThyCouponInfo {
    private Integer id;
    /*关联优惠卷模板主键*/
    private Integer templateId;
    /*领取用户*/
    private Long userId;
    /*优惠卷码*/
    private String couponCode;
    /*领取时间*/
    private String assignTime;
    /*优惠卷状态*/
    private String status;

    /**
     * <h2>优惠卷实体转为 ThyCouponInfo</h2>
     * @param coupon
     * @return
     */
    static ThyCouponInfo to(Coupon coupon){
        ThyCouponInfo info = new ThyCouponInfo();
        info.setId(coupon.getId());
        info.setTemplateId(coupon.getTemplateId());
        info.setUserId(coupon.getUserId());
        info.setCouponCode(coupon.getCouponCode());
        info.setAssignTime(new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss").format(coupon.getAssignTime()));
        info.setStatus(coupon.getStatus().getDescription());
        return info;
    }

}
