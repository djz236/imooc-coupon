package org.imooc.coupon.dao;

import org.imooc.coupon.entity.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <h1>UserRoleMapping Dao</h1>
 * @Author: crowsjian
 * @Date: 2020/6/26 18:51
 */
public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping,Integer> {

    /**
     * <h2>通过 userId 寻找数据记录</h2>
     * @param userId
     * @return
     */
    UserRoleMapping findByUserId(Long userId);
}
