package org.imooc.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <h1>操作模式的枚举定义</h1>
 * @Author: crowsjian
 * @Date: 2020/6/26 17:36
 */
@Getter
@AllArgsConstructor
public enum OpModeEnum {
    READ("读"),
    WRITE("写"),
    ;
    private String mode;
}
