package org.imooc.coupon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * pre：在请求被路由（转发）之前调用
 * route：在路由（请求）转发时被调用
 * error：服务网关发生异常时被调用
 * post：在路由（转发）请求后调用
 * <h1>通用的抽象过滤器类</h1>
 * Created by Qinyi.
 */
public abstract class AbstractZuulFilter extends ZuulFilter {

    //用于在过滤器之间传递消息，数据保存在每个请求的ThreadLocal中
    //扩展了Map
    RequestContext context;
    //标识是否执行下一个过滤器
    private final static String NEXT="next";
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * shouldFilter代表这个过滤器是否生效
     * true代表生效，false代表不生效。那么什么情况下使用不生效呢，不生效干嘛还要写这个filter类呢？
     * 其实是有用的，有时我们会动态的决定让不让一个filter生效，譬如我们可能根据Request里是否携带某个参数来判断是否需要生效，或者我们需要从上一个filter里接收某个数据来决定，再或者我们希望能手工控制是否生效（使用如Appolo之类的配置中心，来动态设置该字段）。
     * @return
     */
    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return (boolean) ctx.getOrDefault(NEXT, true);
    }

    /**
     * Run方法
     * 这个是主要的处理逻辑的地方，我们做权限控制、日志等都是在这里。
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        context= RequestContext.getCurrentContext();
        return cRun();
    }
    protected abstract Object cRun();
    Object fail(int code,String msg){
        context.set(NEXT,false);//失败 可以不去执行其他过滤器了
        context.setSendZuulResponse(false);//失败 可以不去执行其他过滤器了
        context.getResponse().setContentType("text/html;charset=UTF-8");
        context.setResponseStatusCode(code);//传递进来的code
        context.setResponseBody(String.format("{\"result\": \"%s!\"}", msg));
        return null;
    }
    Object success(){
        context.set(NEXT,true);
        return null;
    }
}
