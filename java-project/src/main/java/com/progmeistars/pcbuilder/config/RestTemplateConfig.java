package com.progmeistars.pcbuilder.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5_000);
        requestFactory.setReadTimeout(5_000);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        ClientHttpRequestInterceptor userAgentInterceptor = (request, body, execution) -> {
            request.getHeaders().setAccept(Collections.singletonList(org.springframework.http.MediaType.APPLICATION_JSON));
            request.getHeaders().set(org.springframework.http.HttpHeaders.USER_AGENT, "PcBuilder/1.0");
            return execution.execute(request, body);
        };
        restTemplate.setInterceptors(Collections.singletonList(userAgentInterceptor));
        return restTemplate;
    }
}
