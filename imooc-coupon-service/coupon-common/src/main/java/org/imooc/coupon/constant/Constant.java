package org.imooc.coupon.constant;

/**
 * @author Administrator
 * @description: 常用常量定义
 * @param:
 * @return:
 * @date: 2021/1/19 20:38
 */
public class Constant {
    /**
     * kafka 消息的topic
     */
    public static final String TOPIC = "imooc_user_coupon_op";

    /**
     * @author Administrator
     * @description: redis keu 前缀定义
     * @param:
     * @return:
     * @date: 2021/1/19 20:40
     */
    public static class RedisPrefix {
        /*优惠卷码 key 前缀*/
        public static final String COUPON_TEMPLATE = "imooc_coupon_template_code_";
        /*用户当前所有可用的优惠卷 key 前缀*/
        public static final String USER_COUPON_USABLE = "imooc_user_coupon_usable_";
        /*用户当前已使用的优惠卷 key 前缀*/
        public static final String USER_COUPON_USED = "imooc_user_coupon_used_";
        /*用户当前已过期的优惠卷 key 前缀*/
        public static final String USER_COUPON_EXPIRED = "imooc_user_coupon_expired_";
    }

}
