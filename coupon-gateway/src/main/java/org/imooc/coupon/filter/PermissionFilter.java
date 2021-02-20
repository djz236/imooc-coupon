package org.imooc.coupon.filter;

import com.alibaba.fastjson.JSON;
import org.imooc.coupon.permission.PermissionClient;
import org.imooc.coupon.vo.CheckPermissionRequest;
import org.imooc.coupon.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <h1>权限过滤器的实现</h1>
 */
@Slf4j
@Component
public class PermissionFilter extends AbsSecurityFilter {

    private final PermissionClient permissionClient;

    public PermissionFilter(PermissionClient permissionClient) {
        this.permissionClient = permissionClient;
    }

    @Override
    protected Boolean interceptCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 执行权限校验的逻辑
        // 从 header 中 获取到userId
        Long userId = Long.valueOf(request.getHeader("userId"));
        String uri = request.getRequestURI().substring("/imooc".length());
        String httpMethod = request.getMethod();

        return permissionClient.checkPermission(
                new CheckPermissionRequest(userId,uri,httpMethod)
        );
    }

    @Override
    protected int getHttpStatus() {
        return HttpStatus.OK.value();
    }

    @Override
    protected String getErrorMsg() {
        CommonResponse<Object> response = new CommonResponse<>();
        response.setCode(-2);
        response.setMessage("您没有操作权限");
        return JSON.toJSONString(response);
    }
}
