package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @description: 优惠券模板相关的功能控制器
 * @param:
 * @return:
 * @date: 2021/1/20 21:42
 */
@Slf4j
@RestController
public class CouponTemplateController {
    /*构建优惠卷模板服务*/
    private final IBuildTemplateService buildTemplateService;
    /*优惠卷模板基础服务*/
    private final ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(IBuildTemplateService buildTemplateService, ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    /**
     * @description: 构建优惠卷模板
     * @param: [request]
     * @return: com.imooc.coupon.entity.CouponTemplate
     * @author Administrator
     * @date: 2021/1/20 21:47
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException {
        log.info("Build Template :{}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * @description: 构造优惠卷模板详情
     * @param: [id]
     * @return: com.imooc.coupon.entity.CouponTemplate
     * @author Administrator
     * @date: 2021/1/20 21:49
     */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id) throws CouponException {
        log.info("Build Template Info for :{}", id);
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * @description: 查找所有可用的优惠卷模板
     * @param: []
     * @return: java.util.List<com.immoc.coupon.vo.CouponTemplateSDK>
     * @author Administrator
     * @date: 2021/1/20 21:52
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        log.info("Find All Usable Template.");
        return templateBaseService.findAllUsableTemplate();
    }

    /**
     * @description: 获取模板 ids 到 CouponTemplateSDK 的映射
     * @param: [ids]
     * @return: java.util.Map<java.lang.Integer, com.immoc.coupon.vo.CouponTemplateSDK>
     * @author Administrator
     * @date: 2021/1/20 21:55
     */
    @GetMapping("/template/sdk/infos")
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(
            @RequestParam("ids") Collection<Integer> ids) {
        log.info("findIds2TemplateSDK:{}", JSON.toJSONString(ids));
        return templateBaseService.findIds2TemplateSDK(ids);
    }

}
