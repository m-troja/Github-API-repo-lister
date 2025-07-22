package com.michal.github;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class GithubApplication {

	 @Bean
	    public CloseableHttpClient httpClient() {
	        return HttpClients.createDefault();
	    }
	 
	public static void main(String[] args) {
		SpringApplication.run(GithubApplication.class, args);
	}

}
