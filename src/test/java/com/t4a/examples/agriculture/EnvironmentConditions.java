package com.t4a.examples.agriculture;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class EnvironmentConditions {
    private double waterPercentage;
    private double soilPercentage;
    private double sunlightPercentage;
    private double temperature;
    private boolean hasFrost;
    private boolean hasHeatWave;
    private boolean hasDrought;
    private boolean hasFlood;
}