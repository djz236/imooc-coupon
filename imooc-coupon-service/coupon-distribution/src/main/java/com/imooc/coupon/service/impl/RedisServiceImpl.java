package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {
    /**
     * redis客户端
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {
        log.info("Get Coupon From Cache:{},{}", userId, status);
        String redisKey = status2RedisKey(status, userId);
        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey).stream()
                .map(o -> Objects.toString(o, null)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(couponStrs)) {
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        return couponStrs.stream().map(cs -> JSON.parseObject(cs, Coupon.class)).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("all")
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty List to cache for user :{} Status:{}", userId, status);
        Map<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));
        //使用SessionCallBack 把数据命令当如到redis的pipeline
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);
                });
                return null;
            }
        };
        //使用Pipeline可以批量执行redis命令，防止多个命令建立多个连接
        List<Object> executePipelined = redisTemplate.executePipelined(sessionCallback);
        log.info("Pipeline Exe Result :{}", JSON.toJSONString(executePipelined));
    }

    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, templateId);
        // 因为优惠卷码不存在顺序关系，左边pop或右边pop,没有影响
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire Coupon Code :{},{},{}", templateId, redisKey, couponCode);
        return couponCode;
    }
    /**
     * <h2>将优惠卷保存到 cache 中</h2>
     * @param userId 用户 id
     * @param coupons {@link Coupon}s
     * @param status 优惠卷状态
     * @return 保存成功的个数
     * @throws CouponException
     */
    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupon To Cache:{},{},{}",
                userId,JSON.toJSONString(coupons), status);
        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus){
            case USABLE:
                result = addCouponToCahceForUsable(userId, coupons);
                break;
            case USED:
                result = addCouponToCahceForUsed(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCahceForExpired(userId, coupons);
                break;
        }
        return result;
    }
    /**
     * <h2>将已使用的优惠卷加入到 Cache中</h2>
     * @param userId
     * @param coupons
     * @return
     * @throws CouponException
     */
    @SuppressWarnings("all")
    private Integer addCouponToCahceForUsed(Long userId,List<Coupon> coupons) throws CouponException{
        // 如果 status 是 USED,代表是用户的操作是使用当前的优惠卷,影响到两个 Cache
        // USABLE,USED
        log.debug("Add Coupon To Cahce For Used.");
        Map<String,String> needCachedForUsed = new HashMap<>(coupons.size());
        String redisKeyForUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        String redisKeyForUsed = status2RedisKey(CouponStatus.USED.getCode(), userId);
        // 获取当前用户可用的优惠卷
        List<Coupon> curUsableCoupons = getCachedCoupons(userId,CouponStatus.USABLE.getCode());
        // 当前可用的优惠卷个数一定是大于1的
        assert curUsableCoupons.size() > coupons.size();
        coupons.forEach(c->
                needCachedForUsed.put(c.getId().toString()
                        ,JSON.toJSONString(c))
        );
        //校验当前的优惠卷参数 是否与 Cached 中的匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        //子集
        if(!org.apache.commons.collections.CollectionUtils.isSubCollection(paramIds,curUsableIds)){
            log.error("CurCoupons Is Not Equal To Cache:{},{},{}",
                    userId,JSON.toJSONString(curUsableIds),JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupons Is Not Equal To Cache！");

        }
        List<String> needCleanKey = paramIds.stream().map(i->i.toString()).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations redisOperations) throws DataAccessException {
                //1. 已使用的优惠卷 Cache 缓存
                redisOperations.opsForHash().putAll(redisKeyForUsed,needCachedForUsed);
                //2. 可用的优惠卷 Cache 需要清理
                redisOperations.opsForHash().delete(redisKeyForUsable,needCleanKey.toArray());
                //3.  重置过期时间
                redisOperations.expire(
                        redisKeyForUsed,getRandomExpirationTime(1,2),TimeUnit.SECONDS
                );
                redisOperations.expire(
                        redisKeyForUsable,getRandomExpirationTime(1,2),TimeUnit.SECONDS
                );
                return null;
            }
        };
        log.info("Pipeline Exe Result:{}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }
    private String status2RedisKey(Integer status, Long userId) {
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USABLE, userId);
                break;
            case USED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USED, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
                break;
        }
        return redisKey;
    }
    /**
     * <h2>获取一个随机的过期时间</h2>
     * 缓存雪崩：key 在同一时间失效
     * @param min 最小的小时数
     * @param max 做大的小时数
     * @return 返回[min,max] 之间的随机秒数
     */
    private Long getRandomExpirationTime(Integer min, Integer max){
        return RandomUtils.nextLong(min*60*60,max*60*60);
    }
    /**
     * <h2>将过期优惠卷加入到 Cache 中</h2>
     * @param userId
     * @param coupons
     * @return
     * @throws CouponException
     */
    @SuppressWarnings("all")
    private Integer addCouponToCahceForExpired(Long userId,List<Coupon> coupons) throws CouponException{
        // status 是 EXPIRED, 代表是已有的优惠卷过期了，影响到两个 Cache
        // USABLE, EXPIRED
        log.debug("Add Coupon To Cache  For Expired.");
        //最终需要保存的 Cache
        Map<String, String> needCachedForExpired= new HashMap<>(coupons.size());
        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisKeyForExpired = status2RedisKey(
                CouponStatus.EXPIRED.getCode(), userId
        );
        List<Coupon> curUsableCoupons = getCachedCoupons(
                userId, CouponStatus.USABLE.getCode()
        );
        // 当前可用的优惠卷一定是大于1的
        assert curUsableCoupons.size() > coupons.size();
        coupons.forEach(c->needCachedForExpired.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));
        // 校验当前的优惠卷参数是否与 Cache 中相匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        if(!org.apache.commons.collections.CollectionUtils.isSubCollection(paramIds,curUsableIds)){
            log.error("Coupon Is Not Equal To Cache:{},{},{}",
                    userId, JSON.toJSONString(curUsableIds),
                    JSON.toJSONString(paramIds));
            throw new CouponException("Coupon Is Not Equal To Cache.");
        }
        List<String> needCleanKey = paramIds.stream().map(i->i.toString()).collect(Collectors.toList());
        SessionCallback sessionCallback = new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 1.已过期的 Cache 缓存
                redisOperations.opsForHash().putAll(
                        redisKeyForExpired,needCachedForExpired
                );
                // 2. 可用的优惠卷 Cache 需要清理
                redisOperations.opsForHash().delete(
                        redisKeyForUsable,needCleanKey.toArray()
                );
                //  3. 重置过期时间
                redisOperations.expire(
                        redisKeyForUsable,
                        getRandomExpirationTime(1,2),
                        TimeUnit.SECONDS
                );
                redisOperations.expire(
                        redisKeyForExpired,
                        getRandomExpirationTime(1,2),
                        TimeUnit.SECONDS
                );
                return null;
            }
        };
        log.info("Pipeline Exe Result:{}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }
    /**
     * <h2>新增加优惠卷到 cache 中</h2>
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouponToCahceForUsable(Long userId,List<Coupon> coupons){
        // 如果 status 是 USABLE,代表是新增加的优惠卷
        // 只会影响一个 Cache:USER_COUPON_USABLE
        log.debug("Add Coupon To Cahce For Usable.");
        Map<String,String> needCacheObject = new HashMap<>();
        coupons.forEach(c->needCacheObject.put(c.getId().toString(), JSON.toJSONString(c)));
        String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        //以map集合的形式添加键值对。
        redisTemplate.opsForHash().putAll(redisKey,needCacheObject);
        log.info("Add {} Coupons To Cache:{},{}",needCacheObject.size(),userId, redisKey);
        //有时候希望给添加的缓存设置生命时间，到期后自动删除该缓存。可以使用 key是键，需要是已经存在的，seconds是时间，单位是long类型，最后一个参数单位。
        redisTemplate.expire(redisKey, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
        return needCacheObject.size();
    }
}
