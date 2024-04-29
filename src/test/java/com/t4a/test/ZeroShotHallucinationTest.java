package com.t4a.test;

import com.t4a.api.GuardRailException;
import com.t4a.detect.*;
import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiV2ActionProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ZeroShotHallucinationTest {

    private static String sampleResponse = "Mohandas Karamchand Gandhi (ISO: Mōhanadāsa Karamacaṁda Gāṁdhī;[pron 1] 2 October 1869 – 30 January 1948) was an Indian lawyer, anti-colonial nationalist and political ethicist who employed nonviolent resistance to lead the successful campaign for India's independence from British rule. He inspired movements for civil rights and freedom across the world. The honorific Mahātmā (from Sanskrit 'great-souled, venerable'), first applied to him in South Africa in 1914, is now used throughout the world. Born and raised in a Hindu family in coastal Gujarat, Gandhi trained in the law at the Inner Temple in London, and was called to the bar in June 1891, at the age of 22. After two uncertain years in India, where he was unable to start a successful law practice, he moved to South Africa in 1893 to represent an Indian merchant in a lawsuit. He went on to live in South Africa for 21 years. There, Gandhi raised a family and first employed nonviolent resistance in a campaign for civil rights. In 1915, aged 45, he returned to India and soon set about organising peasants, farmers, and urban labourers to protest against discrimination and excessive land-tax.";
    @Test
    void testHallucination() throws GuardRailException, AIProcessingException {
        GeminiV2ActionProcessor processor = Mockito.mock(GeminiV2ActionProcessor.class);
        when(processor.processSingleAction(anyString())).thenReturn("good anser");
        when(processor.processSingleAction(anyString(),any(HallucinationAction.class))).thenReturn("good anser");
        DetectValues dv = new DetectValues();
        dv.setResponse(sampleResponse);
        ZeroShotHallucinationDetector detec = new ZeroShotHallucinationDetector(processor);
        DetectValueRes res =  detec.execute(dv);
        Assertions.assertNull(res.getHallucinationList());
    }

    @Test
    void testHallucinationProcess() throws GuardRailException, AIProcessingException {
        GeminiV2ActionProcessor processor = Mockito.mock(GeminiV2ActionProcessor.class);
        when(processor.processSingleAction(anyString())).thenReturn("good anser");
        when(processor.query(anyString())).thenReturn("good anser");
        List<HallucinationQA> list = new ArrayList<>();;
        list.add(new HallucinationQA("question","answer","context","truth"));
        when(processor.processSingleAction(anyString(),any(HallucinationAction.class))).thenReturn(list);
        when(processor.processSingleAction(anyString(),any(HallucinationAction.class))).thenReturn(list);
        DetectValues dv = new DetectValues();
        dv.setResponse(sampleResponse);
        ZeroShotHallucinationDetector detec = new ZeroShotHallucinationDetector(processor);
        DetectValueRes res =  detec.execute(dv);
        Assertions.assertEquals("truth",res.getHallucinationList().get(0).getTruthPercentage());
    }
}
