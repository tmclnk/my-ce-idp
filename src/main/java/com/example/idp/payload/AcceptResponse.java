package com.example.idp.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AcceptResponse {
    @JsonProperty("redirect_to")
    private String redirectTo;
}
