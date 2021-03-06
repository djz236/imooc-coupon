package org.imooc.coupon.permission;

import org.imooc.coupon.vo.CheckPermissionRequest;
import org.imooc.coupon.vo.CommonResponse;
import org.imooc.coupon.vo.CreatePathRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * <h1>路径创建与权限校验 Feign 接口实验</h1>
 */
@FeignClient(value = "eureka-client-coupon-permission")
public interface PermissionClient {

    @RequestMapping(value = "/coupon-permission/create/path", method = RequestMethod.POST)
    CommonResponse<List<Integer>> createPath(@RequestBody CreatePathRequest request);

    @RequestMapping(value = "/coupon-permission/check/permission",method = RequestMethod.POST)
    Boolean checkPermission(@RequestBody CheckPermissionRequest request);
}
