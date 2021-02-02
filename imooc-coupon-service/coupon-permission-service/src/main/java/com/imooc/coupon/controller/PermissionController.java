package com.imooc.coupon.controller;

import com.imooc.coupon.annotation.IgnoreResponseAdvice;
import com.imooc.coupon.service.PathService;
import com.imooc.coupon.service.PermissionService;
import com.imooc.coupon.vo.CheckPermissionRequest;
import com.imooc.coupon.vo.CreatePathRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <h1>路径创建与权限校验对外服务接口实现</h1>
 * @Author: crowsjian
 * @Date: 2020/6/27 17:40
 */
@Slf4j
@RestController
public class PermissionController {

    private final PathService pathService;

    private final PermissionService permissionService;

    public PermissionController(PathService pathService, PermissionService permissionService) {
        this.pathService = pathService;
        this.permissionService = permissionService;
    }

    /**
     *<h2>路径创建接口</h2>
     * @param request {@link CreatePathRequest}
     * @return
     */
    @PostMapping("/create/path")
    public List<Integer> createPath(@RequestBody CreatePathRequest request){
        log.info("CreatePath:{}",request.getPathInfos().size());
        return pathService.createPath(request);
    }

    /**
     * <h2>权限校验接口</h2>
     * @param request
     * @return
     */
    @IgnoreResponseAdvice
    @PostMapping("/check/permission")
    public Boolean checkPermission(@RequestBody CheckPermissionRequest request){
        log.info("Check Permission For args:{},{},{}",
                request.getUserId(),request.getUri(),request.getHttpMethod());
        return permissionService.checkPermission(
                request.getUserId(),request.getUri(),request.getHttpMethod()
        );
    }
}
