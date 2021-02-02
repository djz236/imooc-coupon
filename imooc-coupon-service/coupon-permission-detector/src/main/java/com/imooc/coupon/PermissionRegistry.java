package com.imooc.coupon;

import com.imooc.coupon.permission.PermissionClient;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.CreatePathRequest;
import com.imooc.coupon.vo.PermissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>权限注册组件</h1>
 * @Author: crowsjian
 * @Date: 2020/6/29 18:58
 */
@Slf4j
public class PermissionRegistry {
    /*权限服务SDK客户端*/
    private PermissionClient permissionClient;

    /*服务名称*/
    private String serviceName;

    /**
     * <h2>构造方法</h2>
     * @param permissionClient
     * @param serviceName
     */
    PermissionRegistry(PermissionClient permissionClient,String serviceName){
        this.permissionClient = permissionClient;
        this.serviceName = serviceName;
    }

    /**
     * <h2>权限注册</h2>
     * @param infoList
     * @return
     */
    boolean register(List<PermissionInfo> infoList){
        if(CollectionUtils.isEmpty(infoList)){
            return false;
        }
        List<CreatePathRequest.PathInfo> pathInfos = infoList.stream()
                .map(info-> CreatePathRequest.PathInfo.builder()
                            .pathPattern(info.getUrl())
                            .httpMethod(info.getMethod())
                            .pathName(info.getDescription())
                            .serviceName(serviceName)
                            .opMode(info.getIsRead()?OpModeEnum.READ.name():OpModeEnum.WRITE.name())
                            .build()
                ).collect(Collectors.toList());


        CommonResponse<List<Integer>> response = permissionClient.createPath(
                new CreatePathRequest(pathInfos)
        );
        if(!CollectionUtils.isEmpty(response.getData())){
            log.info("register path info:{}",response.getData());
            return true;
        }
        return false;
    }
}
