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
public class Plant {
    @Prompt(describe ="what type is it CROP or FRUIT or VEGETABLE or FLOWER or TREE or SHRUB or GRASS or WEED or OTHER")
    private String platType;
    private String plantName;
    private HealthStatus healthStatus;
    private EnvironmentConditions environmentConditions;
    private boolean hasWaterDeficiency = false;;
    private boolean hasSunlightDeficiency = false;;
    private boolean hasSoilDeficiency = false;;
    private boolean hasTemperatureDeficiency = false;;
}

