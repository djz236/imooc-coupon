package org.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * 在所有的过滤器之前进行校验
 * <h1>校验请求中传递的 Token</h1>
 */
@Slf4j
//@Component
public class TokenFilter extends AbstractPreZuulFilter {

    @Override
    protected Object cRun() {

        HttpServletRequest request = context.getRequest();
        log.info(String.format("%s request to %s",
                request.getMethod(), request.getRequestURL().toString()));

        Object token = request.getParameter("token");
        if (null == token) {
            log.error("error: token is empty");
            //401 用户没有权限访问
            return fail(401, "error: token is empty");
        }

        return success();
    }

    /**
     * filterOrder() must also be defined for a filter. Filters may have the same  filterOrder if precedence is not
     * important for a filter. filterOrders do not need to be sequential.
     *优先级
     * @return the int order of a filter
     */
    @Override
    public int filterOrder() {
        return 1;
    }
}
