package com.imooc.coupon.dao;

import com.imooc.coupon.entity.RolePathMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1>RolePathMapping Dao</h1>
 * @Author: crowsjian
 * @Date: 2020/6/26 18:49
 */
public interface RolePathMappingRepository extends JpaRepository<RolePathMapping,Integer> {
    /**
     * <h2>根据 角色id + 路径id 寻找数据记录</h2>
     * @param roleId
     * @param pathId
     * @return
     */
    RolePathMapping findByRoleIdAndPathId(Integer roleId,Integer pathId);
}
