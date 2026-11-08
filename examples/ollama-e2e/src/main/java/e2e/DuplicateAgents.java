package e2e;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;

/** Two agents exposing the same action name, to exercise the duplicate-registration warning. */
public class DuplicateAgents {

    @Agent(groupName = "diagnostics", groupDescription = "System diagnostics")
    public static class FirstPinger {
        @Action(description = "Ping a host")
        public String pingHost(String host) {
            return "first " + host;
        }
    }

    @Agent(groupName = "diagnostics", groupDescription = "System diagnostics")
    public static class SecondPinger {
        @Action(description = "Ping a host")
        public String pingHost(String host) {
            return "second " + host;
        }
    }
}
