package com.t4a.action;

import lombok.*;

@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ExtendedInputParameter {
    @NonNull
    private String name;
    @NonNull
    private String type;
    private String defaultValueStr;
    private boolean hasDefaultValue;

    public boolean hasDefaultValue(){
        return hasDefaultValue;
    }
}
