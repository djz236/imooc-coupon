package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <h1>用户角色枚举</h1>
 * @Author: crowsjian
 * @Date: 2020/6/27 17:14
 */
@Getter
@AllArgsConstructor
public enum  RoleEnum {
    ADMIN("管理员"),
    SUPER_ADMIN("超级管理员"),
    CUSTOMER("普通用户"),
    ;

    private String roleName;
}
