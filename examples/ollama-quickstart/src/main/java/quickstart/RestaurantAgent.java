package quickstart;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;

/**
 * An agent is a plain Java class. Each {@code @Action} method is a tool the model can choose;
 * Tools4AI picks the method that fits the prompt and fills in its parameters.
 */
@Agent(groupName = "restaurant", groupDescription = "Restaurant table bookings and menu questions")
public class RestaurantAgent {

    @Action(description = "Book a table at the restaurant")
    public String bookTable(String customerName, int numberOfPeople, String day) {
        return "Booked a table for " + numberOfPeople + " under " + customerName + " on " + day;
    }

    @Action(description = "Check whether a dish is on today's menu")
    public String isOnMenu(String dish) {
        boolean available = dish.toLowerCase().contains("pasta") || dish.toLowerCase().contains("pizza");
        return dish + (available ? " is on the menu today" : " is not on the menu today");
    }
}
