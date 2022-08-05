package com.inspien.fb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class StatementResponse {
    public StatementResponse() {}
    public StatementResponse(int status, String error_code, String error_message) {
        this.status = status;
        this.error_code = error_code;
        this.error_message = error_message;
    }

    private int status;
    private String request_at;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String error_code;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String error_message;
}