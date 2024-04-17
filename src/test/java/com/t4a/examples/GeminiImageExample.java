package com.t4a.examples;

import com.t4a.examples.pojo.AutoRepairScreen;
import com.t4a.examples.pojo.MyGymSchedule;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiImageActionProcessor;
import lombok.extern.java.Log;

@Log
public class GeminiImageExample {

    public static void main(String[] args) throws AIProcessingException {
        GeminiImageActionProcessor processor = new GeminiImageActionProcessor();
        String jsonStr = processor.imageToJson(GeminiImageExample.class.getClassLoader().getResource("auto.PNG"),"Full Inspection");
        log.info(jsonStr);
        jsonStr = processor.imageToJson(GeminiImageExample.class.getClassLoader().getResource("auto.PNG"),"Full Inspection","Tire Rotation","Oil Change");
        log.info(jsonStr);
        jsonStr = processor.imageToJson(GeminiImageExample.class.getClassLoader().getResource("auto.PNG"), AutoRepairScreen.class);
        log.info(jsonStr);
        jsonStr = processor.imageToJson(GeminiImageExample.class.getClassLoader().getResource("fitness.PNG"), MyGymSchedule.class);
        log.info(jsonStr);
        Object pojo = processor.imageToPojo(GeminiImageExample.class.getClassLoader().getResource("fitness.PNG"), MyGymSchedule.class);
        log.info(pojo.toString());
        pojo = processor.imageToPojo(GeminiImageExample.class.getClassLoader().getResource("auto.PNG"), AutoRepairScreen.class);
        log.info(pojo.toString());
    }

}
