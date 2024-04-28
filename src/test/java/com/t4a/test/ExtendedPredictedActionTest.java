package com.t4a.test;

import com.t4a.action.ExtendedPredictedAction;
import com.t4a.predict.LoaderException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ExtendedPredictedActionTest {

    @Test
    public void testExtendedPredictedAction() throws LoaderException {
        // Create a mock of ExtendedPredictedAction
        ExtendedPredictedAction actionMock = Mockito.mock(ExtendedPredictedAction.class);

        // Define the behavior of the mock
        when(actionMock.getInputParameters()).thenReturn(Collections.emptyList());
        when(actionMock.extendedExecute(any(Map.class))).thenReturn("Test Result");

        // Call the methods of the mock
        assertEquals(Collections.emptyList(), actionMock.getInputParameters());
        assertEquals("Test Result", actionMock.extendedExecute(Collections.emptyMap()));

        // Verify that the methods were called with the correct parameters
        verify(actionMock, times(1)).getInputParameters();
        verify(actionMock, times(1)).extendedExecute(any(Map.class));
    }
}