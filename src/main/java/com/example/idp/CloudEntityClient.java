package com.example.idp;

import com.example.idp.payload.AcceptRequest;
import com.example.idp.payload.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Component
@Slf4j
@ConfigurationProperties(prefix = "cloudentity")
public class CloudEntityClient {
    // Set the config properties below using spring application.yaml (or equivalent).
    @Setter
    private String issuerUri;
    @Setter
    private String authServer;
    @Setter
    private String clientId;
    @Setter
    private String clientSecret;

    /**
     * POST to the CloudEntity "/accept" url.
     * Uses a Bearer token obtained using client credentials.
     * There's no refresh token here, we're fetching a new Bearer token every time.
     */
    public void accept(String subject, LoginCommand command) {
        var accessToken = getAccessToken();
        var payload = new AcceptRequest(subject, command.getLoginState());
        var url = String.format("%s/logins/%s/accept", issuerUri, command.getLoginId());
        log.trace(url);
        log.trace(accessToken);
        var httpClient = HttpClient.create().wiretap(true);
        log.trace(toJSON(payload));
        var result = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build()
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", String.format("Bearer %s", accessToken))
                .body(BodyInserters.fromValue(payload))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(String.class);

        // TODO get the redirect_to value from the response payload
        log.info("{}", result.block());
    }

    /**
     * Fetches a bearer token from token endpoint using client-id and client-secret.
     * @throws RuntimeException if there's an error response from the token service, or if the accessToken
     * is null or blank.
     */
    private String getAccessToken() {
        var url = String.format("%s/oauth2/token", authServer);
        log.trace(url);
        var response = WebClient.create().method(HttpMethod.POST)
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, resp -> resp.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(TokenResponse.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Empty token response"));

        log.trace(toJSON(response));
        if (response.getAccessToken() == null || response.getAccessToken().isBlank()) {
            throw new RuntimeException("Access token value missing from response");
        }
        return response.getAccessToken();
    }

    private String toJSON(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
