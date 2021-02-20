package org.imooc.coupon.executor;

import org.imooc.coupon.vo.SettlementInfo;
import org.imooc.coupon.constant.RuleFlag;

/**
 * @description: <h1>优惠卷模板规则处理器接口定义</h1>
 * @param:  
 * @return:  
 * @author Administrator
 * @date: 2021/1/25 16:02
 */ 
public interface RuleExecutor {
    /**
     * <h2>规则类型标记</h2>
     * @return
     */
    RuleFlag ruleConfig();

    /**
     * <h2>优惠卷规则的计算</h2>
     * @param settlement {@link SettlementInfo} 包含了选择的优惠卷
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    SettlementInfo computeRule(SettlementInfo settlement);

}
