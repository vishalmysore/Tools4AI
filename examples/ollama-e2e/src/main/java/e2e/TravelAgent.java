package e2e;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;

@Agent(groupName = "travel", groupDescription = "Travel bookings: search flights to a destination city")
public class TravelAgent {
    public static volatile String lastCall;

    // Overload declared first and NOT annotated: before the fix the processor could pick this one by name
    public String searchFlights(int flightNumber) {
        lastCall = "int:" + flightNumber;
        return lastCall;
    }

    @Action(description = "Search for flights to the given destination city")
    public String searchFlights(String destinationCity) {
        lastCall = "string:" + destinationCity;
        return lastCall;
    }
}
