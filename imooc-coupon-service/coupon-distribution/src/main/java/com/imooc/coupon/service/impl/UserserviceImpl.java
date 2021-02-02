package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CouponKafkaMessage;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.dao.CouponDao;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.feign.SettlementClient;
import com.imooc.coupon.feign.TemplateClient;
import com.imooc.coupon.service.IRedisService;
import com.imooc.coupon.service.IUserService;
import com.imooc.coupon.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <h1>用户服务相关的接口实现</h1>
 * 所有的操作过程，状态都保存在redis中，并通过 Kafka 把消息传递到 MySql 中
 * 为什么使用 Kafka，而不是直接使用Springboot 中的异步处理？ 失败后kafka 可以有好的解决方案 高可用
 */
@Slf4j
@Service
public class UserserviceImpl implements IUserService {
    /*coupon Dao 接口*/
    private final CouponDao couponDao;
    /*Redis 服务*/
    private final IRedisService redisService;

    /*模板微服务客户端*/
    private final TemplateClient templateClient;

    /*结算微服务客户端*/
    private final SettlementClient settlementClient;

    /*Kafka 客户端*/
    private final KafkaTemplate<String,String> kafkaTemplate;

    public UserserviceImpl(CouponDao couponDao, IRedisService redisService,
                           TemplateClient templateClient,
                           SettlementClient settlementClient,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
        this.kafkaTemplate = kafkaTemplate;
    }


    /**
     * <h2>根据用户id和状态查询优惠卷记录</h2>
     * @param userId 用户 id
     * @param status 优惠卷状态
     * @return {@link Coupon}s
     * @throws ClassCastException
     */
    @Override
    public List<Coupon> findCouponByStatus(Long userId, Integer status) throws CouponException {
        // 缓存优惠卷信息
        List<Coupon> curCached = redisService.getCachedCoupons(userId,status);
        List<Coupon> preTarget;
        if(!CollectionUtils.isEmpty(curCached)){
            log.debug("coupon cache is not empty:{},{}",userId,status);
            preTarget = curCached;
        } else {
            log.debug("coupon cache is empty, get coupon from db:{},{}",
                    userId,status);
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(userId, CouponStatus.of(status));
            // 如果数据库中没有记录，直接返回就可以，Cache 中已经加入了一张无效的优惠卷
            if(CollectionUtils.isEmpty(dbCoupons)){
                log.debug("current user do not have coupon,{},{}",userId,status);
                return dbCoupons;
            }
            // 填充 dbCoupons 的 templateSDK字段
            Map<Integer, CouponTemplateSDK> id2templateSDK = templateClient.findIds2TemplateSDK(
                    dbCoupons.stream().map(Coupon::getTemplateId).collect(Collectors.toList())
            ).getData();
            dbCoupons.forEach(dc->{
                dc.setTemplateSDK(id2templateSDK.get(dc.getTemplateId()));
            });
            // 数据库中存在记录
            preTarget = dbCoupons;
            // 将记录写入 Cache
            redisService.addCouponToCache(userId,preTarget,status);
        }
        // 将无效优惠卷去除
        preTarget = preTarget.stream()
                .filter(c->c.getId() != -1)
                .collect(Collectors.toList());
        // 如果当前获取的是可用优惠卷，还需要做对已过期优惠卷的延迟处理
        if(CouponStatus.of(status) == CouponStatus.USABLE){
            CouponClassify classify = CouponClassify.classify(preTarget);
            //如果已过期状态不为空，需要做延迟处理
            if(!CollectionUtils.isEmpty(classify.getExpired())){
                log.info("Add Expired Coupon To Cache From FinfCouponsByStatus:{},{}",userId,status);
                redisService.addCouponToCache(userId,classify.getExpired(),CouponStatus.EXPIRED.getCode());
                // 发送到 Kafka中做异步处理
                kafkaTemplate.send(
                        Constant.TOPIC,
                        JSON.toJSONString(new CouponKafkaMessage(
                                CouponStatus.EXPIRED.getCode(),
                                classify.getExpired().stream().map(Coupon::getId).collect(Collectors.toList()))
                        )
                );
            }
            return classify.getUsable();
        }
        return preTarget;
    }

    /**
     * <h2>根据用户id查到当前可以领取的优惠卷模板</h2>
     * @param userId 用户 id
     * @return {@link CouponTemplateSDK}s
     * @throws ClassCastException
     */
    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {
        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKS = templateClient.findAllUsableTemplate().getData();
        log.debug("Find All Template(From TemplateClient) Count:{}",templateSDKS.size());
        // 过滤过期的优惠卷模板
        templateSDKS = templateSDKS.stream().filter(
                t->t.getRule().getExpiration().getDeadline() > curTime
        ).collect(Collectors.toList());
        log.info("Find Usable Template Count:{}",templateSDKS.size());
        //key 是 templateId
        //value 中的 left 是 Template Limitation领取的次数上限，right 是优惠卷模板
        Map<Integer, Pair<Integer,CouponTemplateSDK>> limit2Template = new HashMap<>(templateSDKS.size());
        templateSDKS.forEach(
                t-> limit2Template.put(
                        t.getId(),
                        Pair.of(t.getRule().getLimitation(),t)
                )
        );
        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
        List<Coupon> userUsableCoupons = findCouponByStatus(userId,CouponStatus.USABLE.getCode());
        log.debug("Current User Has Usable Coupons:{},{}",userId,userUsableCoupons.size());
        //key 是 templateId
        Map<Integer,List<Coupon>> templateId2Coupons = userUsableCoupons.stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        // 根据 Template 的 Rule 判断是否可以领取 优惠卷模板
        limit2Template.forEach((k,v)->{
            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();
            if(templateId2Coupons.containsKey(k)
                &&templateId2Coupons.get(k).size()>=limitation){
                return;
            }
            result.add(templateSDK);
        });
        return result;
    }

    /**
     * <h2>用户领取优惠卷</h2>
     * 1.从TemplateClient 中拿到对应的优惠卷 ，并检查是否过期
     * 2.根据limitation 判断用户是否可以领取
     * 3.save to db
     * 4.填充 CouponTemplateSDK
     * 5.save to cache
     * @param request {@link AcquireTemplateRequest}
     * @return {@link Coupon}
     * @throws CouponException
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {
        Map<Integer,CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(
                Collections.singletonList(request.getTemplateSDK().getId())
        ).getData();
        // 优惠卷模板是需要存在的
        if(id2Template.size()<=0){
            log.error("Can Not Aquire Template From TemplateClient:{}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can Not Aquire Template From TemplateClient");
        }
        // 用户是否可以领取这张优惠卷
        List<Coupon> userUsableCoupons = findCouponByStatus(request.getUserId(),CouponStatus.USABLE.getCode());
        Map<Integer,List<Coupon>> templateId2Coupons = userUsableCoupons.stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        if(templateId2Coupons.containsKey(request.getTemplateSDK().getId())
            &&templateId2Coupons.get(request.getTemplateSDK().getId()).size()>=
                request.getTemplateSDK().getRule().getLimitation()){
            log.error("Exceed Template Assign Limitation:{}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign Limitation");
        }
        //尝试去获取优惠卷码
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(request.getTemplateSDK().getId());
        if(StringUtils.isEmpty(couponCode)){
            log.error("Can Not Aquire Coupon Code:{}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign Limitation");
        }
        Coupon newCoupon = new Coupon(
            request.getTemplateSDK().getId(),request.getUserId(),
                couponCode,CouponStatus.USABLE
        );
        newCoupon = couponDao.save(newCoupon);
        //填充 Coupon 对象的 CouponTemplateSDK,一定要在放入缓存之前去填充
        newCoupon.setTemplateSDK(request.getTemplateSDK());
        // 放入缓存中
        redisService.addCouponToCache(
                request.getUserId()
                ,Collections.singletonList(newCoupon)
                ,CouponStatus.USABLE.getCode()
        );
        return newCoupon;
    }

    /**
     * <h2>结算（核销） 优惠卷</h2>
     * 这里需要注意，规则相关处理需要由 Settlement 系统去做，当前系统仅仅做
     * 业务处理过程（校验过程）
     * @param info @link SettlementInfo}
     * @return @link SettlementInfo}
     * @throws CouponException
     */
    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        // 当没有传递优惠卷时，直接返回商品总价
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos =
                info.getCouponAndTemplateInfos();
        if(CollectionUtils.isEmpty(ctInfos)){
            log.info("Empty Coupons For Settle.");
            double goodSum = 0.0;
            for(GoodsInfo gi:info.getGoodsInfos()){
                goodSum += gi.getPrice() * gi.getCount();
            }
            // 没有优惠卷 也就不存在优惠卷的核销，SettlementInfo  其他字段不需要修改
            info.setCost(retain2Decimals(goodSum));
        }
        // 校验传递的优惠卷是否是用户自己的
        List<Coupon> coupons = findCouponByStatus(
                info.getUserId(),CouponStatus.USABLE.getCode()
        );
        Map<Integer,Coupon> id2Coupon = coupons.stream()
                .collect(Collectors.toMap(
                        Coupon::getId,
                        Function.identity()
                ));
        if(id2Coupon.isEmpty()|| org.apache.commons.collections.CollectionUtils.isSubCollection(
                ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId).collect(Collectors.toList()),
                id2Coupon.keySet()
        )){
            log.info("{}",id2Coupon.keySet());
            log.info("{}", ctInfos.stream().map(SettlementInfo.CouponAndTemplateInfo::getId).collect(Collectors.toList()));
            log.error("User Coupon Has Some Problem,It Is Not SubCollection Of Coupons!");
            throw  new CouponException("User Coupon Has Some Problem,It Is Not SubCollection Of Coupons!");
        }
        log.debug("Current Settlemen Coupons Is User's:{}",ctInfos.size());
        List<Coupon> settleCoupons =  new ArrayList<>(ctInfos.size());
        ctInfos.forEach(ci->settleCoupons.add(id2Coupon.get(ci.getId())));
        // 通过结算服务，获取结算信息
        SettlementInfo processedInfo = settlementClient.computeRule(info).getData();
        if(processedInfo.getEmploy()&&!CollectionUtils.isEmpty(processedInfo.getCouponAndTemplateInfos())){
            log.info("Settle User Coupon:{},{}",info.getUserId(),
                    JSON.toJSONString(settleCoupons));
            //更新缓存
            redisService.addCouponToCache(
                    info.getUserId(),
                    settleCoupons,
                    CouponStatus.USED.getCode()
            );
            // 更新db
            kafkaTemplate.send(
                    Constant.TOPIC,
                    JSON.toJSONString(new CouponKafkaMessage(
                            CouponStatus.USED.getCode(),
                            settleCoupons.stream().map(Coupon::getId).collect(Collectors.toList())
                    ))
            );
        }
        return processedInfo;
    }

    /**
     * <h2>保留两位小数</h2>
     * @param value
     * @return
     */
    private double retain2Decimals(Double value){
        return new BigDecimal(value)
                .setScale(2,BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }
}
