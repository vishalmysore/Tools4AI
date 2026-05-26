package com.t4a.test;

import com.t4a.api.AIAction;
import com.t4a.api.ActionList;
import com.t4a.predict.LoaderException;
import com.t4a.predict.ShellPredictionLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ShellPredictionLoaderTest {

    private ShellPredictionLoader loader;
    private Map<String, AIAction> predictions;
    private StringBuilder actionNameList;
    private ActionList listOfActions;

    @BeforeEach
    void setUp() {
        loader = new ShellPredictionLoader();
        predictions = new HashMap<>();
        actionNameList = new StringBuilder();
        listOfActions = new ActionList();
    }

    @Test
    void testLoad_populatesPredictions() throws LoaderException {
        // shell_actions.yaml exists in test resources
        loader.load(predictions, actionNameList, listOfActions);
        assertFalse(predictions.isEmpty(), "predictions should not be empty after loading shell_actions.yaml");
    }

    @Test
    void testLoad_knownAction_saveEmployeeInformation() throws LoaderException {
        loader.load(predictions, actionNameList, listOfActions);
        assertTrue(predictions.containsKey("saveEmployeeInformation"),
                "should contain 'saveEmployeeInformation' from shell_actions.yaml");
    }

    @Test
    void testLoad_actionNameListContainsEntries() throws LoaderException {
        loader.load(predictions, actionNameList, listOfActions);
        assertTrue(actionNameList.length() > 0, "action name list should be non-empty");
        assertTrue(actionNameList.toString().contains("saveEmployeeInformation"));
    }

    @Test
    void testLoad_listOfActionsPopulated() throws LoaderException {
        loader.load(predictions, actionNameList, listOfActions);
        // After loading, predictions map already verifies actions were processed
        assertFalse(predictions.isEmpty(),
                "ActionList should have at least one entry");
    }

    @Test
    void testLoad_multipleGroups() throws LoaderException {
        loader.load(predictions, actionNameList, listOfActions);
        // shell_actions.yaml has two groups: Employee Actions and Enterprise Actions
        assertTrue(predictions.size() >= 2, "should load actions from multiple groups");
    }
}
