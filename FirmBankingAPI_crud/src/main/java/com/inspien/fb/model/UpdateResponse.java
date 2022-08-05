package com.inspien.fb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.configurationprocessor.json.JSONObject;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UpdateResponse {
    private JSONObject applications;
}
