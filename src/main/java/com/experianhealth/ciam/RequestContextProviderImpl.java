package com.experianhealth.ciam;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequestContextProviderImpl implements RequestContextProvider, RequestContextProviderManager
{
    private static final ThreadLocal<RequestContext> requestContextThreadLocal = new ThreadLocal<>();
    @Override
    public Optional<RequestContext> getRequestContext() {
       return Optional.ofNullable(requestContextThreadLocal.get());
    }
    @Override
    public void setRequestContext(RequestContext requestContext){
        requestContextThreadLocal.set(requestContext);
    }

    @Override
    public void reset() {
        requestContextThreadLocal.remove();
    }
}
