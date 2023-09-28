package com.experianhealth.ciam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ContextInterceptor implements HandlerInterceptor {

    RequestContextProviderManager contextProvider ;

    ContextInterceptor(@Autowired RequestContextProviderManager contextProvider){
        this.contextProvider = contextProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        System.out.println("Intercepting the request");
        contextProvider.reset();
        RequestContext rc = RequestContext.Builder.create().fromHttpServletRequest(httpServletRequest).build();
        System.out.println(rc);
        contextProvider.setRequestContext(rc);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        contextProvider.reset();
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        contextProvider.reset();
    }
}
