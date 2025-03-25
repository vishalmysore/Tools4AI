package com.t4a.examples.agriculture;

import com.t4a.annotations.Prompt;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class HealthStatus {
    @Prompt(describe ="if it looks healthy then give 100% else give a percentage")
    private double healthPercentage;
    private boolean hasPest;
    private boolean hasDisease;
    private boolean hasNutrientDeficiency;
    private boolean doesItHaveAnyDisease;
    @Prompt(describe ="based on picture do you think the plant has any disease, if none then return NONE")
    private String typeOfDisease;
}