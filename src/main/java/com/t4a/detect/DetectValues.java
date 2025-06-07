package com.t4a.detect;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DetectValues {
    private String prompt;
    private String context;
    private String response;
    private String additionalData;
}
