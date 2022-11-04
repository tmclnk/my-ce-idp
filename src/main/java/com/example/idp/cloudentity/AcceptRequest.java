package com.example.idp.cloudentity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
class AcceptRequest {
    @JsonProperty("auth_time")
    private String authTime;
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("login_state")
    private String loginState;

    /**
     * Additional properties which go into the CloudEntity authentication context.
     */
    @JsonProperty("authentication_context")
    private Map<String, Object> authenticationContext = new HashMap<>();

    public AcceptRequest(String subject, String loginState) {
        this.subject = subject;
        this.authTime = DateTimeFormatter.ISO_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS));
        this.loginState = loginState;
        return;
    }
}
