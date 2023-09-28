package com.experianhealth.ciam;

import java.util.Optional;

public interface RequestContextProvider {

    Optional<RequestContext> getRequestContext();
}
