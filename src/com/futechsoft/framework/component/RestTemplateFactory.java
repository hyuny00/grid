package com.futechsoft.framework.component;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author futechSoft
 *
 */
@Component("component.RestTemplateFactory")
public class RestTemplateFactory {

    RestTemplate restTemplate = null;

    @PostConstruct
    public void init() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        HttpClient client = HttpClientBuilder.create()
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                .setMaxConnTotal(300)
                .setMaxConnPerRoute(100)
               // .evictIdleConnections(5, TimeUnit.SECONDS)
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(15000);
        factory.setConnectTimeout(10000);
        factory.setHttpClient(client);

        restTemplate = new RestTemplate(factory);
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();

		converters.addAll(restTemplate.getMessageConverters());
		for (Iterator<HttpMessageConverter<?>> iterator = converters.iterator(); iterator.hasNext(); ) {
		    HttpMessageConverter<?> converter = iterator.next();
		    if (converter instanceof StringHttpMessageConverter) {
		        iterator.remove();
		    }
		}
        converters.add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        restTemplate.setMessageConverters(converters);

    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}


