package org.imooc.coupon.schedule;


import org.imooc.coupon.vo.TemplateRule;
import org.imooc.coupon.dao.CouponTemplateDao;
import org.imooc.coupon.entity.CouponTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1>定时清理已过期的优惠卷模板</h1>
 * @Author: crowsjian
 * @Date: 2020/6/8 20:47
 */
@Slf4j
@Component
public class ScheduledTask {
    /*CouponTemplate Dao*/
    private final CouponTemplateDao templateDao;

    public ScheduledTask(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /**
     * <h1>下线已过期的优惠卷模板</h1>
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void offlinCouponTemplate(){
        log.info("Start To Expire CouponTmeplate.");
        List<CouponTemplate> templates = templateDao.findAllByExpired(false);
        if(CollectionUtils.isEmpty(templates)){
            log.info("Done To Expire CouponTmeplate.");
            return;
        }
        Date cur = new Date();
        List<CouponTemplate> expiredTemplates = new ArrayList<>(templates.size());
        templates.stream()
                .forEach(t->{
                    // 根据优惠卷模板中规则中的 “过期规则” 校验模板是否过期
                    TemplateRule rule = t.getRule();
                    if(rule.getExpiration().getDeadline() < cur.getTime()){
                        t.setExpired(true);
                        expiredTemplates.add(t);
                    }
                });
        if(!CollectionUtils.isEmpty(expiredTemplates)){
            log.info("Expired CouponTemplate Num:{}", templateDao.saveAll(expiredTemplates));
        }
        log.info("Done To Expire CouponTmeplate.");
    }
}
