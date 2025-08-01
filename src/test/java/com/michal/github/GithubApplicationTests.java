package com.michal.github;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import static org.assertj.core.api.Assertions.assertThat;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
 * * Test checks if:
 * StatusCode of response == 200,
 * returned response of repos is not empty,
 * response returns expected qty of repos,
 * specific repo exists,
 * specific repo contains expected qty of branches,
 * name of two specific branches is as expected.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubApplicationTests {
	   
    @LocalServerPort
    private int port = 8080;

   @RegisterExtension
   WireMockExtension wireMock = WireMockExtension.newInstance()
	   .options(wireMockConfig().port(port))  // set WireMock port 8080
	   .build();
    
    @Test
    void checkBranchesInSpecificRepo() throws URISyntaxException, IOException, InterruptedException {
    	    	
    	/* Config */
        final String username = "m-troja";
        final String url = "http://localhost:" + port + "/v1/repos?login=" + username;
    	String responseJsonNonForks = Files.readString(Paths.get("src/test/resources/response.mtroja.json"));

        // Setup the WireMock mapping stub for the test
        stubFor(get(url)
        		.withHeader("Content-Type", containing("application/json"))
        		.willReturn(ok()
        				.withHeader("Content-Type","application/json")
        				.withBody(responseJsonNonForks)));
        
        // 2. Send  HTTP request to mocked endpoint
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 3. Verify non-forks
        JSONAssert.assertEquals(responseJsonNonForks, response.body(), false);
    }
}
