package com.t4a.action.http;

import lombok.*;

/**
 * Maps to Input parameter in the config file, this is the input which will be sent to HTTP request
 */
@Setter
@Getter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class InputParameter {
    /**
     * Name of the input will be used as is in the request for http
     */
    private String name;
    /**
     * can be either path_parameter, query_parameter or body
     */
    private String type;
    private String description;
    private String defaultValue;
    private ParamLocation location;


    public InputParameter(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public boolean hasDefaultValue() {
        return defaultValue!= null;
    }


}