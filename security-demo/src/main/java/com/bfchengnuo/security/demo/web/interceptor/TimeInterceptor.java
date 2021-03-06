package com.bfchengnuo.security.demo.web.interceptor;

import com.bfchengnuo.security.demo.web.config.WebConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器，与过滤器不同的是，它是由 Spring 提供，能够获取到 SpringMVC 的相关信息
 * <p>
 * 关于 postHandle 和 afterCompletion 简单说：
 * 它们都是在调用之后执行，不过 postHandle 在抛出异常时不会被调用，而 afterCompletion 会继续调用
 *
 * 请注意，MVC 的异常处理优于 afterCompletion，如果进行过异常处理，这里是拿不到异常的。
 *
 * PS：只声明 Component 是没有效果的，
 * 还是要在 {@link WebConfig} 进行配置
 *
 * @see com.bfchengnuo.security.demo.web.filter.TimeFilter
 * @see com.bfchengnuo.security.demo.web.aspect.TimeAspect
 *
 * @author Created by 冰封承諾Andy on 2019/7/14.
 */
@Component
public class TimeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("TimeInterceptor preHandle");
        request.setAttribute("start", System.currentTimeMillis());

        // 是否继续执行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("TimeInterceptor postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("TimeInterceptor afterCompletion");
        long start = (long) request.getAttribute("start");
        String name = "not HandlerMethod";
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            name = handlerMethod.getMethod().getName();
        }
        System.out.println("TimeInterceptor-" + name + "-耗时：" + (System.currentTimeMillis() - start));
    }
}
