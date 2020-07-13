package com.nasnav.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Response {

    @JsonProperty("Saved")
    private boolean saved;

    @JsonProperty("Error Message")
    private String errorMessage;
}
