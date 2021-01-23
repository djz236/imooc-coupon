package com.imooc.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.immoc.coupon.constant.Constant;
import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <h1>异步服务接口实现</h1>
 * @Author: crowsjian
 * @Date: 2020/6/7 15:41
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {
    /*CouponTemplat Dao接口*/
    private final CouponTemplateDao templateDao;
    /*注入 Redis 模板类*/
    private final StringRedisTemplate redisTemplate;
    //使用构造方法注入两个对象
    public AsyncServiceImpl(CouponTemplateDao templateDao, StringRedisTemplate redisTemplate) {
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }

    /**
     * <h2>根据模板异步的创建优惠卷码</h2>
     * @param template {@link CouponTemplate} 优惠卷模板实体
     */
    @Async("getAsyncExecutor")
    @SuppressWarnings("all")
    @Override
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Set<String> couponCodes = buildCoouponCode(template);
        // imooc_coupon_template_code_1
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());
        log.info("Push CouponCode To Redis:{}",
                redisTemplate.opsForList().rightPushAll(redisKey,couponCodes));
        template.setAvailable(true);
        templateDao.save(template);
        stopwatch.stop();
        log.info("Construct CouponCode By Template Cost:{}ms",
                stopwatch.elapsed(TimeUnit.SECONDS));
        // TODO 发送短信或邮件通知优惠卷模板已经可用
        log.info("CouponTemplate({}) Is Available!", template.getId());
    }

    /**
     * <h2>构造优惠卷码</h2>
     * 优惠卷码（对应于每一张优惠卷，18位）
     *   前四位：产品线 + 类型
     *   中间六位：日期随机（190101）
     *   后八位：0~9 随机数构成
     * @param template {@link CouponTemplate} 优惠卷模板实体
     * @return Set<String> 与 template.count 相同个数的优惠卷码
     */
    @SuppressWarnings("all")
    private Set<String> buildCoouponCode(CouponTemplate template){
        //StopWatch是位于org.springframework.util包下的一个工具类，通过它可方便的对程序部分代码进行计时(ms级别)，适用于同步单线程代码块。
        Stopwatch stopwatch = Stopwatch.createStarted();//
        Set<String> result = new HashSet<>(template.getCount());
        //前四位
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();

        String date = new SimpleDateFormat("yyMMdd")
                .format(template.getCreateTime());
        for(int i = 0; i != template.getCount(); ++i){
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }//有可能重复 个数不够  写while

        while(result.size() < template.getCount()){
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        assert result.size() == template.getCount();
        stopwatch.stop();

        log.info("Build Coupon Code Cost:{}ms",
                stopwatch.elapsed(TimeUnit.SECONDS));
        return result;
    }

    /**
     * <h2>构造优惠卷码的后14位</h2>
     * @param date 创建优惠卷的日期
     * @return 14 位 优惠卷码
     */
    private String buildCouponCodeSuffix14(String date){
        char[] bases = new char[]{'1','2','3','4','5','6','7','8','9'};
        //中间六位
        List<Character> chars = date.chars()
                .mapToObj(e->(char)e).collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6 = chars.stream()
                .map(Objects::toString).collect(Collectors.joining());
        //后八位
        String suffix8 = RandomStringUtils.random(1,bases)
                + RandomStringUtils.randomNumeric(7);
        return mid6 + suffix8;
    }
}
