package org.imooc.coupon.vo;

import org.imooc.coupon.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: 优惠券规则定义
 * @author Administrator
 * @date 2020/12/30 22:19
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRule {
    /*优惠卷过期规则*/
    private Expiration expiration;
    /*折扣*/
    private Discount discount;
    /*每个人最多领几张的限制*/
    private Integer limitation;
    /*适用范围：地域 + 商品类型*/
    private Usage usage;

    /*权重（可以和哪些优惠卷叠加使用，同一类的优惠卷一定不能叠加）：list[]， 优惠卷的唯一编码
     * */
    private String weight;

    /**
     * <h2>校验功能</h2>
     * @return
     */
    public boolean validate(){
        return expiration.validate() && discount.validate()
                && limitation > 0 && usage.validate()
                && StringUtils.isNotEmpty(weight);
    }

    /**
     * @description: 有效期规则
     * @author Administrator
     * @date 2020/12/30 22:22
     * @version 1.0
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Expiration{
        //有效期规则 对应PeriodType 的 code
        private Integer period;
        //有效间隔 只对变动性有效期有效
        private Integer gap;
        /**
         * 优惠券模板的失效日期，两类规则都有效
         */
        private Long deadline;
        boolean validate(){
            //最简化校验
            return null!= PeriodType.of(period)&&gap>0&&deadline>0;
        }
    }
    /**
     * @description: 折扣，需要与类型配合决定
     * @author Administrator
     * @date 2020/12/30 22:28
     * @version 1.0
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class Discount{
        /**
         * 额度：满减（20），折扣（85），立减（10）
         */
        private Integer quota;
        /**
         * 基准，需要满多少才可用
         */
        private Integer base;
        boolean validate(){
            return quota>0&&base>0;
        }
    }
    /**
     * @description: 使用范围
     * @author Administrator
     * @date 2020/12/30 22:32
     * @version 1.0
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class Usage{
        /**
         * 省份
         */
        private String province;
        /**
         * 城市
         */
        private String city;
        /**
         * 商品类型，list[文娱，生鲜，家具]
         */
        private String goodsType;

        boolean validate(){
            return StringUtils.isNotEmpty(province)&&StringUtils.isNotEmpty(city)&&StringUtils.isNotEmpty(goodsType);
        }
    }
}
