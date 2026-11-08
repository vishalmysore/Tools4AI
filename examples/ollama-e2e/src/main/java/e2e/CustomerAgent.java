package e2e;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.api.ActionRisk;
import com.t4a.detect.ActionCallback;

@Agent(groupName = "customer", groupDescription = "Manage customer records: add new customers, remove customers")
public class CustomerAgent {
    // Plain field: injected by the restored reflection fallback in AIProcessor.setCallBack
    public ActionCallback callback;

    public static volatile String lastSaved;
    public static volatile boolean callbackSeen;
    public static volatile boolean deleteRan;

    @Action(description = "Add a new customer with their full name and the city they live in")
    public String saveCustomer(String customerName, String city) {
        lastSaved = customerName + "|" + city;
        callbackSeen = callback != null;
        return "saved " + lastSaved;
    }

    @Action(description = "Delete every customer record from the database", riskLevel = ActionRisk.HIGH)
    public String deleteAllCustomers(String reason) {
        deleteRan = true;
        return "deleted all customers";
    }
}
