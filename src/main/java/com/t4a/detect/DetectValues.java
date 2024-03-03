package com.t4a.detect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DetectValues {
    private String prompt;
    private String context;
    private String response;
    private String additionalData;
}
