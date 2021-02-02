package com.imooc.coupon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>权限忽略注解：忽略当前标识的Controller 接口的权限</h1>
 * @Author: crowsjian
 * @Date: 2020/6/28 21:32
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnorePermission {
}
