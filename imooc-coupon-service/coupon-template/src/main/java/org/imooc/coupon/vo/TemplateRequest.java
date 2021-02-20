package org.imooc.coupon.vo;

import org.imooc.coupon.constant.CouponCategory;
import org.imooc.coupon.constant.DistributeTarget;
import org.imooc.coupon.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/** 
 * @description: 优惠卷模板创建请求对象
 * @param:  
 * @return:  
 * @author Administrator
 * @date: 2021/1/18 19:41
 */ 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {
    /*优惠卷名称*/
    private String name;
    /*优惠卷logo*/
    private String logo;
    /*优惠卷描述*/
    private String desc;
    /*优惠卷分类*/
    private String category;
    /*产品线*/
    private Integer productLine;
    /*总数*/
    private Integer count;
    /*创建用户*/
    private Long userId;
    /*目标用户*/
    private Integer target;
    /*优惠卷规则*/
    private TemplateRule rule;

    /**
     * <h2>校验对象的合法性</h2>
     * @return
     */
    public boolean validate(){
        boolean stringValid = StringUtils.isNotBlank(name)
                && StringUtils.isNotBlank(logo)
                && StringUtils.isNotBlank(desc);
        boolean enumValid = null != CouponCategory.of(category)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(productLine);
        boolean numValid = count > 0 && target>0;
        return stringValid && enumValid && numValid && rule.validate();
    }
}
