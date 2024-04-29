package com.t4a.regression;

import com.t4a.api.AIPlatform;
import com.t4a.api.GuardRailException;
import com.t4a.detect.DetectValueRes;
import com.t4a.detect.DetectValues;
import com.t4a.detect.ZeroShotHallucinationDetector;
import com.t4a.processor.AIProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ZeroShotHallucinationValidation {

    private static String sampleResponse = "Mohandas Karamchand Gandhi (ISO: Mōhanadāsa Karamacaṁda Gāṁdhī;[pron 1] 2 October 1869 – 30 January 1948) was an Indian lawyer, anti-colonial nationalist and political ethicist who employed nonviolent resistance to lead the successful campaign for India's independence from British rule. He inspired movements for civil rights and freedom across the world. The honorific Mahātmā (from Sanskrit 'great-souled, venerable'), first applied to him in South Africa in 1914, is now used throughout the world. Born and raised in a Hindu family in coastal Gujarat, Gandhi trained in the law at the Inner Temple in London, and was called to the bar in June 1891, at the age of 22. After two uncertain years in India, where he was unable to start a successful law practice, he moved to South Africa in 1893 to represent an Indian merchant in a lawsuit. He went on to live in South Africa for 21 years. There, Gandhi raised a family and first employed nonviolent resistance in a campaign for civil rights. In 1915, aged 45, he returned to India and soon set about organising peasants, farmers, and urban labourers to protest against discrimination and excessive land-tax.";
    @Test
    void testHallucination() throws GuardRailException, AIProcessingException {
        DetectValues dv = new DetectValues();
        dv.setResponse(sampleResponse);
        ZeroShotHallucinationDetector detec = new ZeroShotHallucinationDetector(AIPlatform.GEMINI);
        DetectValueRes res =  detec.execute(dv);
        Assertions.assertNotNull(res.getHallucinationList().get(0).getTruthPercentage());
    }
}
