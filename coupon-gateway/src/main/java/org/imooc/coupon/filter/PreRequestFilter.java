package org.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <h1>在过滤器中存储客户端发起请求的时间戳</h1>
 */
@Slf4j
@Component
public class PreRequestFilter extends AbstractPreZuulFilter {

    @Override
    protected Object cRun() {

        context.set("startTime", System.currentTimeMillis());//记录请求进来的时间戳

        return success();
    }

    /**
     * filterOrder() must also be defined for a filter. Filters may have the same  filterOrder if precedence is not
     * important for a filter. filterOrders do not need to be sequential.
     *请求一进来开始记录时间戳 所以级别是最高的
     * @return the int order of a filter
     */
    @Override
    public int filterOrder() {
        return 0;
    }
}
