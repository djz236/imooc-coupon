package org.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>优惠卷 Kafka 消息对象定义</h1>
 * @Author: crowsjian
 * @Date: 2020/6/20 9:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponKafkaMessage {
    /*优惠卷状态*/
    private Integer status;
    /*Coupon 主键*/
    private List<Integer> ids;
}
