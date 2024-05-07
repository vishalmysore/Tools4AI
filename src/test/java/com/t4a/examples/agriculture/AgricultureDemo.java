package com.t4a.examples.agriculture;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiImageActionProcessor;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class AgricultureDemo {
    public static void main(String[] args) throws AIProcessingException, MalformedURLException {
        GeminiImageActionProcessor processor = new GeminiImageActionProcessor();
        String urlString ="https://raw.githubusercontent.com/spMohanty/PlantVillage-Dataset/master/raw/color/Corn_(maize)___healthy/00031d74-076e-4aef-b040-e068cd3576eb___R.S_HL%208315%20copy%202.jpg";

        URL url = new URL(urlString);
        log.info("starting analysis of healthy corn plant");
      //  plantAnalsis(processor, url);

        log.info("starting analysis of unhealthy corn plant");
        urlString = "https://raw.githubusercontent.com/spMohanty/PlantVillage-Dataset/master/raw/color/Corn_(maize)___Northern_Leaf_Blight/0079c731-80f5-4fea-b6a2-4ff23a7ce139___RS_NLB%204121.JPG";
        url = new URL(urlString);
        //plantAnalsisPojo(processor, url);

        log.info("starting analysis of a unhealthy peach plant");
        urlString = "https://github.com/spMohanty/PlantVillage-Dataset/blob/master/raw/color/Peach___Bacterial_spot/004118fe-b351-4cad-83ca-280c77f82eaa___Rutg._Bact.S%201818.JPG?raw=true";
        url = new URL(urlString);
        plantAnalsisPojo(processor, url);


    }
    public static void plantAnalsis(GeminiImageActionProcessor processor, URL url) throws AIProcessingException {
        String plantDetails = (String) processor.imageToJson(url, "cropType", "plantName", "healthPercentage", "waterPercentage", "soilPercentage", "sunlightPercentage", "temperature", "hasPest", "hasDisease", "hasNutrientDeficiency", "hasWaterDeficiency", "hasSunlightDeficiency", "hasSoilDeficiency", "hasTemperatureDeficiency", "hasFrost", "hasHeatWave", "hasDrought", "hasFlood", "typeOfDisease");
        log.info(plantDetails.toString());
        log.info(processor.imageToText(url, "based on image what action should i take on this plant?"));
    }
    public static void plantAnalsisPojo(GeminiImageActionProcessor processor, URL url) throws AIProcessingException {
        Plant plantDetails = (Plant) processor.imageToPojo(url, Plant.class);
        log.info(plantDetails.toString());
        log.info(processor.imageToText(url, "based on image what action should i take on this plant?"));
    }
}
