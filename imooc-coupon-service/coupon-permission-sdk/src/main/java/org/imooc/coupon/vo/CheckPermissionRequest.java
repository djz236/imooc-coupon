package org.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>权限校验请求对象定义</h1>
 * @Author: crowsjian
 * @Date: 2020/6/26 17:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckPermissionRequest {
    private Long userId;
    private String uri;
    private String httpMethod;

}
