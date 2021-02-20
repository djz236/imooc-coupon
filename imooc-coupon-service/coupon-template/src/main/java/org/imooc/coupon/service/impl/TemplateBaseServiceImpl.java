package org.imooc.coupon.service.impl;

import org.imooc.coupon.exception.CouponException;
import org.imooc.coupon.vo.CouponTemplateSDK;
import org.imooc.coupon.dao.CouponTemplateDao;
import org.imooc.coupon.entity.CouponTemplate;
import org.imooc.coupon.service.ITemplateBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description: 优惠券模板基础服务接口实现
 * @param:
 * @return:
 * @date: 2021/1/19 21:53
 */
@Slf4j
@Service
public class TemplateBaseServiceImpl implements ITemplateBaseService {

    private final CouponTemplateDao couponTemplateDao;

    @Autowired
    public TemplateBaseServiceImpl(CouponTemplateDao couponTemplateDao) {
        this.couponTemplateDao = couponTemplateDao;
    }

    /**
     * <h2>根据优惠卷模板id 获取优惠卷模板信息</h2>
     *
     * @param id 模板 id
     * @return {@link CouponTemplate}优惠卷模板实体
     * @throws CouponException
     */
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = couponTemplateDao.findById(id);
        if (!template.isPresent()) {
            throw new CouponException("Template is not exist :" + id);
        }
        return template.get();
    }

    /**
     * <h2>查找所有可用的优惠卷模板</h2>
     *
     * @return {@link CouponTemplateSDK}s
     */
    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        List<CouponTemplate> templates = couponTemplateDao
                .findAllByAvailableAndExpired(true, false);

        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toList());
    }

    /**
     * <h2>获取模板 ids 到 CouponTemplateSDK 的映射</h2>
     *
     * @param ids 模板ids
     * @return Map<key: 模板 id, value: CouponTemplateSDK>
     */
    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate> templates = couponTemplateDao.findAllById(ids);
        return templates.stream().map(this::template2TemplateSDK).collect(Collectors.toMap(
                CouponTemplateSDK::getId, Function.identity()));
    }

    /**
     * <h2>CouponTemplate 转为 CouponTemplateSDK</h2>
     *
     * @param template
     * @return
     */
    private CouponTemplateSDK template2TemplateSDK(CouponTemplate template) {
        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
                template.getKey(),// 并不是拼装好的 Template key
                template.getTarget().getCode(),
                template.getRule()

        );
    }
}
