package com.imooc.coupon.service.impl;

import com.immoc.coupon.exception.CouponException;
import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.service.IAsyncService;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <h1>构建优惠卷模板接口实现</h1>
 *
 * @Author: crowsjian
 * @Date: 2020/6/7 23:29
 */
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {

    private final IAsyncService asyncService;

    private final CouponTemplateDao templateDao;

    @Autowired
    public BuildTemplateServiceImpl(IAsyncService asyncService, CouponTemplateDao templateDao) {
        this.asyncService = asyncService;
        this.templateDao = templateDao;
    }

    /**
     * <h2>创建优惠卷模板</h2>
     *
     * @param templateRequest {@link TemplateRequest} 模板信息请求对象
     * @return {@link CouponTemplate} 优惠卷模板实体
     * @throws CouponException
     */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest templateRequest) throws CouponException {
        // 参数合法性校验
        if (!templateRequest.validate()) {
            throw new CouponException("BuildTemplate Param Is Not Valid!");
        }
        // 判断同名优惠卷模板是否存在
        if (null != templateDao.findByName(templateRequest.getName())) {
            throw new CouponException("Exist Same Name Template!");
        }
        // 构造 CouponTemplate 并保存到 数据库中
        CouponTemplate template = request2Template(templateRequest);
        template = templateDao.save(template);
        // 根据优惠卷模板异步生成优惠卷码
        asyncService.asyncConstructCouponByTemplate(template);
        return template;
    }

    /**
     * <h2>将 TemplateRequest 转为 CouponTemplate</h2>
     *
     * @param templateRequest
     * @return
     */
    private CouponTemplate request2Template(TemplateRequest templateRequest) {
        return new CouponTemplate(
                templateRequest.getName(),
                templateRequest.getLogo(),
                templateRequest.getDesc(),
                templateRequest.getCaetgory(),
                templateRequest.getProductLine(),
                templateRequest.getCount(),
                templateRequest.getUserId(),
                templateRequest.getTarget(),
                templateRequest.getRule()
        );
    }
}
