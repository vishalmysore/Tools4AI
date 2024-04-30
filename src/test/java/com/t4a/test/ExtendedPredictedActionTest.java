package com.t4a.test;

import com.t4a.action.ExtendedInputParameter;
import com.t4a.action.ExtendedPredictedAction;
import com.t4a.api.ActionType;
import com.t4a.predict.LoaderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtendedPredictedActionTest {

    @Test
    public void testExtendedPredictedAction() throws LoaderException {
        // Create a mock of ExtendedPredictedAction
        ExtendedPredictedAction actionMock = new ExtendedPredictedAction() {
            @Override
            public List<ExtendedInputParameter> getInputParameters() {
                ArrayList<ExtendedInputParameter> inputParameters = new ArrayList<>();
                inputParameters.add(new ExtendedInputParameter("param1", "String"));
                return inputParameters;
            }



            @Override
            public Object extendedExecute(Map<String, Object> params) throws LoaderException {
                return params;
            }

            @Override
            public String getActionName() {
                return "MockAction";
            }

            @Override
            public String getDescription() {
                return "Mock action Description";
            }

            @Override
            public String getActionGroup() {
                return "Mock Group";
            }

            @Override
            public String getGroupDescription() {
                return "Mock Description";
            }
        };

        Assertions.assertEquals("MockAction", actionMock.getActionName());
        Assertions.assertEquals("Mock action Description", actionMock.getDescription());
        Assertions.assertEquals("Mock Group", actionMock.getActionGroup());
        Assertions.assertEquals("Mock Description", actionMock.getGroupDescription());
        Assertions.assertEquals(ActionType.EXTEND, actionMock.getActionType());



    }
}