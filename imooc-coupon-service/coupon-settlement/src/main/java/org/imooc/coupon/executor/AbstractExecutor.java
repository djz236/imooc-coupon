package org.imooc.coupon.executor;

import com.alibaba.fastjson.JSON;
import org.imooc.coupon.vo.GoodsInfo;
import org.imooc.coupon.vo.SettlementInfo;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>规则执行器抽象类，定义通用方法</h1>
 */
public abstract class AbstractExecutor {
    /**
     * <h2>校验商品类型与优惠卷是否匹配</h2>
     * 需要注意：
     * 1.这里实现的单品类优惠卷的校验，多品类优惠卷重载此方法
     * 2.商品只需要有一个优惠卷要求的商品类型 去匹配就可以
     * @param settlement
     * @return
     */
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement){
        List<Integer> goodsType = settlement.getGoodsInfos().stream()
                .map(GoodsInfo::getType)
                .collect(Collectors.toList());
        List<Integer> templateGoodsType = JSON.parseObject(
                settlement.getCouponAndTemplateInfos().get(0)
                        .getTemplateSDK().getRule().getUsage().getGoodsType(),
                List.class
        );
        // 存在交集即可
        return !CollectionUtils.isEmpty(
                org.apache.commons.collections.CollectionUtils.intersection(goodsType,templateGoodsType)
        );
    }
    /**
     * <h2>处理商品类型与优惠卷限制不匹配的情况</h2>
     * @param settlementInfo {@link SettlementInfo} 用户传递的结算信息
     * @param goodsSum 商品总价
     * @return {@link SettlementInfo} 已经修改过的结算信息
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(SettlementInfo settlementInfo,Double goodsSum){
        boolean isGoodsTypeSatisfy = isGoodsTypeSatisfy(settlementInfo);
        // 当商品类型不满足时，直接返回总价，并清空优惠卷
        if(!isGoodsTypeSatisfy){
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        }
        return null;
    }

    /**
     * <h2>商品总价</h2>
     * @param goodsInfos
     * @return
     */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos){
        return goodsInfos.stream().mapToDouble(g->g.getCount()*g.getPrice())
                .sum();
    }

    /**
     * <h2>保留两位小数</h2>
     * @param value
     * @return
     */
    protected double return2Decimals(double value){
        return new BigDecimal(value).setScale(
                2, BigDecimal.ROUND_HALF_UP
        ).doubleValue();
    }

    /**
     * <h2>支付最小价格</h2>
     * @return
     */
    protected  double minCost(){
        return 0.1;
    }
}
