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
    String platType;
    String plantName;
    @Prompt(describe ="if it looks healthy then give 100% else give a percentage")
    double healthPercentage;
    double waterPercentage;
    double soilPercentage;
    double sunlightPercentage;
    double temperature;
    boolean hasPest = false;
    boolean hasDisease = false;;
    boolean hasNutrientDeficiency = false;;
    boolean hasWaterDeficiency = false;;
    boolean hasSunlightDeficiency = false;;
    boolean hasSoilDeficiency = false;;
    boolean hasTemperatureDeficiency = false;;
    boolean hasFrost = false;;
    boolean hasHeatWave = false;;
    boolean hasDrought = false;;
    boolean hasFlood= false;;
    boolean doesItHaveAnyDisease = false;
    @Prompt(describe ="based on picture do you think the plant has any disease, if none then return NONE")
    String typeOfDisease;
}

