package org.imooc.coupon.thymeleaf;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.imooc.coupon.dao.CouponDao;
import org.imooc.coupon.entity.Coupon;
import org.imooc.coupon.exception.CouponException;
import org.imooc.coupon.feign.TemplateClient;
import org.imooc.coupon.service.IUserService;
import org.imooc.coupon.vo.AcquireTemplateRequest;
import org.imooc.coupon.vo.CouponTemplateSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h1>优惠卷分发 Controller</h1>
 * @Author: crowsjian
 * @Date: 2020/7/5 10:06
 */
@Slf4j
@Controller
@RequestMapping("/distribution/thy")
public class ThyDistributionController {
    /*Coupon Dao*/
    private final CouponDao couponDao;
    /*用户相关服务*/
    private final IUserService userservice;
    /*模板微服务*/
    private TemplateClient templateClient;

    public ThyDistributionController(CouponDao couponDao, IUserService userservice,TemplateClient templateClient ) {
        this.couponDao = couponDao;
        this.userservice = userservice;
        this.templateClient=templateClient;
    }

    /**
     *<h2>所有用户的优惠卷信息</h2>
     * 127.0.0.1/coupon-distribution/distribution/thy/users
     * @param map
     * @return
     */
    @GetMapping("/users")
    public String users(ModelMap map){
        log.info("view all user coupons.");
        List<Coupon> coupons = couponDao.findAll();
        List<ThyCouponInfo> infos = coupons.stream()
                .map(ThyCouponInfo::to).collect(Collectors.toList());
        map.addAttribute("coupons",infos);
        return "users_coupon_list";
    }

    /**
     * <h2>当前用户的所有优惠卷信息</h2>
     * @param userId
     * @param map
     * @return
     */
    @GetMapping("/user/{userId}")
    public String user(@PathVariable Long userId, ModelMap map){
        log.info("view user:{} coupons",userId);
        List<Coupon> coupons = couponDao.findAllByUserId(userId);
        List<ThyCouponInfo> infos = coupons.stream()
                .map(ThyCouponInfo::to).collect(Collectors.toList());
        map.addAttribute("coupons",infos);
        map.addAttribute("uid", userId);
        return "user_coupon_list";
    }

    /**
     * <h2>用户可以领取的优惠券模板</h2>
     * */
    @GetMapping("/template/{userId}")
    public String template(@PathVariable Long userId, ModelMap map) throws CouponException {

        log.info("view user: {} can acquire template.", userId);

        List<CouponTemplateSDK> templateSDKS = userservice.findAvailableTemplate(userId);
        List<ThyTemplateInfo> infos = templateSDKS.stream()
                .map(ThyTemplateInfo::to).collect(Collectors.toList());
        infos.forEach(i -> i.setUserId(userId));

        map.addAttribute("templates", infos);

        return "template_list";
    }

    @GetMapping("/template/info")
    public String templateInfo(@RequestParam Long uid, @RequestParam Integer id, ModelMap map) {

        log.info("user view template info: {} -> {}", uid, id);

        Map<Integer, CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(
                Collections.singletonList(id)
        ).getData();

        if (id2Template!=null&&id2Template.size()>0) {
            ThyTemplateInfo info = ThyTemplateInfo.to(id2Template.get(id));
            info.setUserId(uid);
            map.addAttribute("template", info);
        }

        return "template_detail";
    }

    @GetMapping("/acquire")
    public String acquire(@RequestParam Long uid, @RequestParam Integer tid) throws CouponException {

        log.info("user {} acquire template {}.", uid, tid);

        Map<Integer, CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(
                Collections.singletonList(tid)
        ).getData();
        if (id2Template!=null&&id2Template.size()>0) {
            log.info("user acquire coupon: {}", JSON.toJSONString(userservice.acquireTemplate(
                    new AcquireTemplateRequest(uid, id2Template.get(tid))
            )));
        }

        return "redirect:/distribution/thy/user/" + uid;
    }
}
