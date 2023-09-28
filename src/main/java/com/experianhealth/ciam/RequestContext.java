package com.experianhealth.ciam;

import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

public class RequestContext {
    public String getBaseUri() {
        return baseUri;
    }

    String baseUri ;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("baseUri", baseUri)
                .toString();
    }

    public static final class Builder {
        private RequestContext context = new RequestContext();
        public static final Builder create() {
            return new Builder();
        }
        public Builder withBaseUri(String uri) {
            context.baseUri = uri;
            return this;
        }

        public Builder fromHttpServletRequest(HttpServletRequest httpServletRequest) {
            context.baseUri = ServletRequestUtil.getBaseUri(httpServletRequest);
            return this;
        }
        public RequestContext build() {
            return context;
        }
    }

    public static final class ServletRequestUtil {
        public static String getBaseUri(HttpServletRequest httpServletRequest){
            UriComponentsBuilder builder = ServletUriComponentsBuilder.fromRequestUri(httpServletRequest).replacePath(null);
            String xforwardProto = httpServletRequest.getHeader("X-FORWARDED-PROTO");
            if(StringUtils.isNotEmpty(xforwardProto)) builder.scheme(xforwardProto);
            return builder.build().toUriString();
        }
    }

}
