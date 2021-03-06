package org.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import org.imooc.coupon.exception.CouponException;
import org.imooc.coupon.vo.CouponTemplateSDK;
import org.imooc.coupon.vo.SettlementInfo;
import org.imooc.coupon.entity.Coupon;
import org.imooc.coupon.service.IUserService;
import org.imooc.coupon.vo.AcquireTemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <h1>用户服务Controller</h1>
 */
@Slf4j
@RestController
public class UserServiceController {
    /*用户服务接口*/
    private final IUserService userService;

    @Autowired
    public UserServiceController(IUserService userService) {
        this.userService = userService;
    }
    /**
     *<h2>根据用户id 和优惠卷状态查找用户优惠卷记录</h2>
     * @param userId
     * @param status
     * @return
     * @throws CouponException
     */
    @GetMapping("/coupons")
    public List<Coupon> findCouponsByStatus(
            @RequestParam("userId") Long userId,
            @RequestParam("status") Integer status
    ) throws CouponException {
        log.info("Find Coupons By Status:{},{}", userId, status);
        return userService.findCouponByStatus(userId, status);
    }
    /**
     * <h2>根据用户id 查找当前可以领取的优惠卷模板</h2>
     * @param userId
     * @return
     */
    @GetMapping("/template")
    public List<CouponTemplateSDK> findAvailableTemplate(@RequestParam("userId") Long userId)
            throws CouponException {
        log.info("Find Available Template:{}",userId);
        return userService.findAvailableTemplate(userId);
    }
    /**
     * <h2>用户领取优惠卷</h2>
     * @param request
     * @return
     * @throws CouponException
     */
    @PostMapping("/acquire/template")
    public Coupon aquireTemplate(@RequestBody AcquireTemplateRequest request) throws CouponException{
        log.info("Aquire Template:{}", JSON.toJSONString(request));
        return userService.acquireTemplate(request);
    }
    /**
     * <h2>结算（核销）优惠卷</h2>
     * @param info
     * @return
     * @throws CouponException
     */
    @PostMapping("/settlement")
    public SettlementInfo settlement(@RequestBody SettlementInfo info) throws CouponException{
        log.info("Settlement:{}",JSON.toJSONString(info));
        return userService.settlement(info);
    }
}
