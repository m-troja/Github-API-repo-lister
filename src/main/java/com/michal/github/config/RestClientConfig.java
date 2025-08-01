package com.michal.github.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${github.token}")
    private String githubToken;
    
    @Value("${github.api.url}")
    private String githubBaseApiUrl;

    @Bean
    public RestClient githubRestClient()
    {
    	 RestClient.Builder builder = RestClient.builder()
    			.baseUrl(githubBaseApiUrl)
    			.defaultHeader("Accept", "application/json");
    	
    	        if (githubToken != null && !githubToken.isBlank()) {
    	        	builder.defaultHeader("Authorization",  "Bearer " + githubToken);
    	        }
    	        
			return builder.build();
		}
}
