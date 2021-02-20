package org.imooc.coupon.vo;

import lombok.Data;

/**\
 * <h1>接口权限信息组装类信息</h1>
 * @Author: crowsjian
 * @Date: 2020/6/28 21:35
 */
@Data
public class PermissionInfo {
    /*Controller 的 URL */
    private String url;

    /*方法类型*/
    private String method;

    /*是否是只读的*/
    private Boolean isRead;

    /*方法描述信息*/
    private String description;

    /*扩展属性*/
    private String extra;

    public String toString(){
        return "url = " + url
                + ",method = " + method
                + ",isRead = " + isRead
                + ",description = " + description;
    }
}
