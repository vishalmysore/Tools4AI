package com.t4a.predict;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Replies below were observed from phi4 via Ollama when asked to pick an action. */
class ActionNameMatchTest {

    private static final String[] NAMES = {"saveCustomer", "deleteAllCustomers", "searchFlights", ""};

    @Test
    void exactAndCaseInsensitive() {
        assertEquals("saveCustomer", PredictionLoader.matchActionName("saveCustomer", NAMES));
        assertEquals("saveCustomer", PredictionLoader.matchActionName("SAVECUSTOMER", NAMES));
    }

    @Test
    void decoratedReplies() {
        assertEquals("deleteAllCustomers", PredictionLoader.matchActionName("`deleteAllCustomers`", NAMES));
        assertEquals("saveCustomer", PredictionLoader.matchActionName("1 saveCustomer", NAMES));
        assertEquals("saveCustomer", PredictionLoader.matchActionName("Save Customer", NAMES));
        assertEquals("deleteAllCustomers", PredictionLoader.matchActionName("Delete All Customers", NAMES));
        assertEquals("searchFlights", PredictionLoader.matchActionName("I would call searchFlights() here.", NAMES));
    }

    @Test
    void ambiguousOrUnknownReturnsNull() {
        assertNull(PredictionLoader.matchActionName("saveCustomer or deleteAllCustomers", NAMES));
        assertNull(PredictionLoader.matchActionName("bookHotel", NAMES));
        assertNull(PredictionLoader.matchActionName("   ", NAMES));
        assertNull(PredictionLoader.matchActionName(null, NAMES));
    }

    @Test
    void groupNamesTolerateVariants() {
        String[] groups = {"customer", "travel", "Employee Actions", "diagnostics"};
        assertEquals("travel", PredictionLoader.matchGroupName("travels  \n", groups));
        assertEquals("Employee Actions", PredictionLoader.matchGroupName("employee_actions", groups));
        assertEquals("customer", PredictionLoader.matchGroupName("'customer'", groups));
        assertNull(PredictionLoader.matchGroupName("billing", groups));
    }

    @Test
    void partialNameIsNotAMatch() {
        // "saveCustomerNotes" must not be read as "saveCustomer"
        assertNull(PredictionLoader.matchActionName("saveCustomerNotes", NAMES));
    }
}
