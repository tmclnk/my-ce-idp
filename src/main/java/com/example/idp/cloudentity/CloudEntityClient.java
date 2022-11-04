package com.example.idp.cloudentity;

import com.example.idp.web.LoginCommand;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.validation.constraints.NotBlank;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Validated
@Slf4j
@ConfigurationProperties(prefix = "cloudentity")
public class CloudEntityClient {
    /**
     * This will be something like
     * https://{{tid}}.us.authz.cloudentity.io/api/system/{{tid}}
     */
    @Setter
    @NotBlank
    private String issuerUri;

    /**
     * This will be something like
     * https://{{tid}}.us.authz.cloudentity.io/{{tid}}/system
     */
    @Setter
    @NotBlank
    private String authServer;

    /**
     * The client-id from the Custom Identity Provider you've registered
     * in the ACP.
     */
    @Setter
    @NotBlank
    private String clientId;
    /**
     * The client-secret from the Custom Identity Provider you've registered
     * in the ACP.
     */
    @Setter
    @NotBlank
    private String clientSecret;

    @Setter
    private Function<LoginCommand, Map<String, Object>> userAttributeService = loginCommand -> {
        var map = new HashMap<String,Object>();
        map.put("my_attribute", "HELLO WORLD!!!!!!!!!!!");
        return map;
    };

    /**
     * POST to the CloudEntity "/accept" url.
     * Uses a Bearer token obtained using client_credentials grant.
     * There's no refresh token here, we're fetching a new Bearer token every time.
     * @return a redirect url (presumably to a consent screen)
     */
    public String accept(String subject, LoginCommand command) {
        // access token is our bearer token
        var accessToken = getAccessToken();
        var accept = new AcceptRequest(subject, command.getLoginState());

        // decorate with custom attributes
        accept.getAuthenticationContext().putAll(userAttributeService.apply(command));

        var url = String.format("%s/logins/%s/accept", issuerUri, command.getLoginId());
        log.trace(url);
        assert accessToken.getScope().contains("manage_logins"): "access token is missing 'manage_logins' scope";

        // create our own netty HttpClient so we have access to tracing the payload
        var httpClient = HttpClient.create().wiretap(true);
        log.trace(toJSON(accept));
        var result = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build()
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", String.format("Bearer %s", accessToken.getAccessToken()))
                .body(BodyInserters.fromValue(accept))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(AcceptResponse.class)
                .block();
        assert result != null && result.getRedirectTo() != null : "missing 'redirect_to' in response";
        return result.getRedirectTo();
    }

    /**
     * Fetches a bearer token from token endpoint using client-id and client-secret.
     * @throws RuntimeException if there's an error response from the token service, or if the accessToken
     * is null or blank.
     */
    private TokenResponse getAccessToken() {
        var url = String.format("%s/oauth2/token", authServer);
        var secret = Base64.getEncoder().encodeToString(String.format("%s:%s", clientId, clientSecret).getBytes());
        var response = WebClient.create().method(HttpMethod.POST)
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", String.format("Basic %s", secret))
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, resp -> resp.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(TokenResponse.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Empty token response"));
        assert response != null && response.getAccessToken() != null : "missing 'access_token' in response";
        return response;
    }

    @SuppressWarnings("java:S112")
    private String toJSON(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
