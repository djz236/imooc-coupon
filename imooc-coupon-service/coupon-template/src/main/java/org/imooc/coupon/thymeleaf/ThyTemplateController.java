package org.imooc.coupon.thymeleaf;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import org.imooc.coupon.constant.*;
import org.imooc.coupon.dao.CouponTemplateDao;
import org.imooc.coupon.entity.CouponTemplate;
import org.imooc.coupon.service.IBuildTemplateService;
import org.imooc.coupon.vo.TemplateRequest;
import org.imooc.coupon.vo.TemplateRule;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h1>优惠卷模板Controller</h1>
 * @Author: crowsjian
 * @Date: 2020/7/4 23:09
 */
@Slf4j
@Controller
@RequestMapping("/template/thy")
public class ThyTemplateController {
    /*CouponTemplate Dao*/
    private final CouponTemplateDao templateDao;
    /*构造优惠卷模板服务*/
    private final IBuildTemplateService templateService;

    public ThyTemplateController(CouponTemplateDao templateDao, IBuildTemplateService templateService) {
        this.templateDao = templateDao;
        this.templateService = templateService;
    }

    /**
     *<h2>优惠卷系统入口</h2>
     * 127.0.0.1：7001/coupon-template/template/thy/home
     * @return
     */
    @GetMapping("/home")
    public String home(){
        log.info("view home.");
        return "home";
    }

    /**
     * <h2>查看优惠卷模板详情</h2>
     * 127.0.0.1：7001/coupon-template/template/thy/info/{id}
     * @return
     */
    @GetMapping("/info/{id}")
    public String info(@PathVariable Integer id, ModelMap map){
        log.info("view template info.");
        Optional<CouponTemplate> templateO =templateDao.findById(id);
        if(templateO.isPresent()){
            CouponTemplate template = templateO.get();
            map.addAttribute("template",ThyTemplateInfo.to(template));
        }
        return "template_detail";
    }

    /**
     * <h2>查看优惠卷模板列表</h2>
     * 127.0.0.1：7001/coupon-template/template/thy/list
     * @param map
     * @return
     */
    @GetMapping("/list")
    public String list(ModelMap map){
        log.info("View Template List.");
        List<CouponTemplate> couponTemplateList = templateDao.findAll();
        List<ThyTemplateInfo> templateInfos = couponTemplateList.stream()
                .map(ThyTemplateInfo::to).collect(Collectors.toList());
        map.addAttribute("templates",templateInfos);
        return "template_list";
    }


    /**
     *<h2>创建优惠卷模板</h2>
     * 127.0.0.1：7001/coupon-template/template/thy/create
     * @param map
     * @param session
     * @return
     */
    @GetMapping("/create")
    public String create(ModelMap map, HttpSession session) {
        log.info("View Template Form.");
        session.setAttribute("category", CouponCategory.values());
        session.setAttribute("productLine", ProductLine.values());
        session.setAttribute("target", DistributeTarget.values());
        session.setAttribute("period", PeriodType.values());
        session.setAttribute("goodsType", GoodsType.values());

        map.addAttribute("template",new ThyCreateTemplate());
        map.addAttribute("action","create");
        return "template_form";
    }

    /**
     * <h2>创建优惠卷模板</h2>
     * 127.0.0.1：7001/coupon-template/template/thy/create
     * @param template
     * @return
     */
    @PostMapping("/create")
    public String create(@ModelAttribute ThyCreateTemplate template) throws Exception {
        log.info("create info.");
        log.info("{}", JSON.toJSONString(template));

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
            template.getPeriod(),template.getGap(),
            new SimpleDateFormat("yyyy-MM-dd").parse(template.getDeadline()).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(template.getQuota(),template.getBase()));
        rule.setLimitation(template.getLimitation());
        rule.setUsage(new TemplateRule.Usage(template.getProvince(),template.getCity(),
                JSON.toJSONString(template.getGoodsType())));
        rule.setWeight(
            JSON.toJSONString(Stream.of(template.getWeight().split(",")).collect(Collectors.toList()))
        );
        TemplateRequest request = new TemplateRequest(
            template.getName(),template.getLogo(),template.getDesc()
                ,template.getCategory(),template.getProductLine(),template.getCount(),
                template.getUserId(),template.getTarget(),rule
        );
        log.info("create coupon template:{}",JSON.toJSONString(templateService.buildTemplate(request)));
        return "redirect:/template/thy/list";
    }
}
