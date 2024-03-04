package com.t4a.predict;

import com.google.gson.JsonObject;
import com.t4a.action.http.InputParameter;
import com.t4a.api.ActionType;
import lombok.*;

import java.util.List;

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

    private String httpUrl;
    private String httpType;
    List<InputParameter> httpInputObjects;
    private JsonObject httpOutputObject;
    private JsonObject httpAuthInterface;

    private ActionType actionType;
    private String scriptPath;

}
