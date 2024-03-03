package com.t4a.predict;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class PredictOptions {
    @NonNull
    private String clazzName;
    @NonNull
    private String description;
    @NonNull
    private String actionName;
    @NonNull
    private String funName;

    private String scriptPath;

}
