package com.example.idp.cloudentity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
class AcceptResponse {
    @JsonProperty("redirect_to")
    private String redirectTo;
}
