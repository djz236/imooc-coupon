package org.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import org.imooc.coupon.exception.CouponException;
import org.imooc.coupon.vo.SettlementInfo;
import org.imooc.coupon.executor.ExecuteManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>结算服务的 Controller</h1>
 */
@Slf4j
@RestController
public class SettlementController {

    /*结算规则执行管理器*/
    private final ExecuteManager executeManager;

    public SettlementController(ExecuteManager executeManager) {
        this.executeManager = executeManager;
    }

    /**
     * <h2>优惠卷结算</h2>
     * 127.0.0.1:9000/imooc/coupon-settlement/settlement/compute
     * @param settlement
     * @return
     * @throws CouponException
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement) throws CouponException {
        log.info("settlementInfo:{}", JSON.toJSONString(settlement));
        return executeManager.computeRule(settlement);
    }
}
