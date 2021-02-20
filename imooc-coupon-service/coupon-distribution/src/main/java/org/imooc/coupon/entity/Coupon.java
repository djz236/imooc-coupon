package org.imooc.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.imooc.coupon.vo.CouponTemplateSDK;
import org.imooc.coupon.constant.CouponStatus;
import org.imooc.coupon.converter.CouponStatusConverter;
import org.imooc.coupon.serialization.CouponSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Administrator
 * @description: 优惠券 用户领取的优惠券记录 实体类
 * @param:
 * @return:
 * @date: 2021/1/22 21:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)//jpa审计功能  实现对列的自动填充
@Table(name = "coupon")
@JsonSerialize(using = CouponSerialize.class)
public class Coupon {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /*关联优惠卷模板的主键（逻辑主键）*/
    @Column(name = "template_id", nullable = false)
    private Integer templateId;
    /*领取用户*/
    @Column(name = "user_id", nullable = false)
    private Long userId;
    /*优惠卷码*/
    @Column(name = "coupon_code", nullable = false)
    private String couponCode;
    /**
     * 领取时间
     */
    @CreatedDate
    @Column(name="assign_time",nullable = false)
    private Date assignTime;
    /**
     * 优惠券状态
     */
    @Column(name = "status",nullable = false)
    @Convert(converter= CouponStatusConverter.class)
    private CouponStatus status;
    /*用户优惠卷模板对应的模板信息*/
    @Transient
    private CouponTemplateSDK templateSDK;
    /**
     * <h2>返回一个无效的 Coupon 对象</h2>
     * @return
     */
    public static Coupon invalidCoupon(){
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }
    /**
     * <h2>构造优惠卷</h2>
     */
    public Coupon(Integer templateId,
                  Long userId,
                  String couponCode,
                  CouponStatus status){
        this.templateId=templateId;
        this.userId=userId;
        this.couponCode=couponCode;
        this.status=status;
    }
}
