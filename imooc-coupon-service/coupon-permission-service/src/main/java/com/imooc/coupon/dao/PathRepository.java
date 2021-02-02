package com.imooc.coupon.dao;

import com.imooc.coupon.entity.Path;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <h1>Path Dao</h1>
 * @Author: crowsjian
 * @Date: 2020/6/26 18:45
 */
public interface PathRepository extends JpaRepository<Path,Integer> {

    /**
     * <h2>根据微服务名称查询Path 记录</h2>
     * @param serviceName
     * @return
     */
    List<Path> findAllByServiceName(String serviceName);

    /**
     * <h2>根据路径模式 + 请求类型 查找数据记录</h2>
     * @param pathParttern
     * @param httpMethod
     * @return
     */
    Path findByPathPartternAndHttpMethod(String pathParttern,String httpMethod);
}
