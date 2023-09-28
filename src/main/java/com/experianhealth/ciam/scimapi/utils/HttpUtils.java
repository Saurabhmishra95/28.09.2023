package com.experianhealth.ciam.scimapi.utils;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

public class HttpUtils {

    public static CloseableHttpClient createAcceptSelfSignedCertificateClient() {
        try {
            // use the TrustSelfSignedStrategy to allow Self Signed Certificates
            SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(
                    SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build(),
                    NoopHostnameVerifier.INSTANCE
            );

            // create an HttpClient using the custom SSL socket factory
            return HttpClients.custom().setSSLSocketFactory(connectionFactory).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create HttpClient with self-signed certificate support", e);
        }
    }
}
