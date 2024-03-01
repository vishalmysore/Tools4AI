package com.t4a.predict;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class PredictOptions {
    private String clazzName;
    private String description;
    private String actionName;
    private String funName;


}
