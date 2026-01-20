package com.t4a.test;

import com.t4a.predict.PredictionLoader;
import com.t4a.api.AIAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;

public class PredictionLoaderCoverageTest {

    @Test
    public void testGetInstance() {
        PredictionLoader loader = PredictionLoader.getInstance();
        Assertions.assertNotNull(loader);
    }

    @Test
    public void testFetchActionNameFromList() {
        PredictionLoader loader = PredictionLoader.getInstance();
        // Since list depends on loaded actions, we can't assert specific content easily
        // without setup
        // But we can test the method logic if we mock or assume emptiness
        // Just calling it safely:
        String result = loader.fetchActionNameFromList("NonExistentAction");
        Assertions.assertNull(result);
    }

    @Test
    public void testGetActionNameList() {
        PredictionLoader loader = PredictionLoader.getInstance();
        StringBuilder list = loader.getActionNameList();
        Assertions.assertNotNull(list);
    }

    @Test
    public void testGetPredictions() {
        PredictionLoader loader = PredictionLoader.getInstance();
        Assertions.assertNotNull(loader.getPredictions());
    }
}
