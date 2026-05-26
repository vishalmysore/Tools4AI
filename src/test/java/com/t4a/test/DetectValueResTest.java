package com.t4a.test;

import com.t4a.detect.DetectValueRes;
import com.t4a.detect.HallucinationQA;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DetectValueResTest {

    @Test
    void testNoArgsConstructor() {
        DetectValueRes res = new DetectValueRes();
        assertNull(res.getHallucinationList());
    }

    @Test
    void testAllArgsConstructor() {
        HallucinationQA qa = new HallucinationQA();
        qa.setQuestion("Is the sky blue?");
        qa.setAnswer("yes");

        List<HallucinationQA> list = Arrays.asList(qa);
        DetectValueRes res = new DetectValueRes(list);

        assertNotNull(res.getHallucinationList());
        assertEquals(1, res.getHallucinationList().size());
        assertEquals("Is the sky blue?", res.getHallucinationList().get(0).getQuestion());
    }

    @Test
    void testSetAndGetHallucinationList() {
        DetectValueRes res = new DetectValueRes();
        HallucinationQA qa1 = new HallucinationQA();
        qa1.setQuestion("q1");
        HallucinationQA qa2 = new HallucinationQA();
        qa2.setQuestion("q2");

        List<HallucinationQA> list = Arrays.asList(qa1, qa2);
        res.setHallucinationList(list);

        assertEquals(2, res.getHallucinationList().size());
    }

    @Test
    void testSetEmptyList() {
        DetectValueRes res = new DetectValueRes();
        res.setHallucinationList(List.of());
        assertNotNull(res.getHallucinationList());
        assertTrue(res.getHallucinationList().isEmpty());
    }
}
