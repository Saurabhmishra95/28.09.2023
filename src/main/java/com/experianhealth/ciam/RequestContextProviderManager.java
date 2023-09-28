package com.experianhealth.ciam;

public interface RequestContextProviderManager extends RequestContextProvider{
    public void setRequestContext(RequestContext requestContext);
    public void reset();

}
