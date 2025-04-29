package com.t4a.api;

import com.t4a.detect.ActionCallback;
import org.json.JSONObject;

/**
 * <pre>
 * This is the key class in creating all the AI related action. All the actions are implementation of this class
 * This follows the functional programming concept and each class can have only one function. Name of the funciton
 * should be descriptive to match the action that function can perform for example
 *
 *
 *  {@code @Agent}
 * public class SimpleAction implements AIAction {
 *
 *     public String whatFoodDoesThisPersonLike(String name) {
 *         return "Paneer Butter Masala";
 *     }
 *     {@code @Override}
 *     public String getActionName() {
 *         return "whatFoodDoesThisPersonLike";
 *     }
 *
 *    {@code @Override}
 *     public ActionType getActionType() {
 *         return ActionType.JAVAMETHOD;
 *     }
 *
 *     {@code @Override}
 *     public String getDescription() {
 *         return "Provide persons name and then find out what does that person like";
 *     }
 *
 *
 *
 * Now lets look at above implementation of AIAction step by step
 *
 * 1) @Agent - This annotation makes sure that AIAction object is our prediction list if you do not mark it with predict
 * annotation then you have to add this action in programmatic way, usually its best to add predict in all the actions
 * which you want to execute automatically, except for some really custom actions which you want AI not to execute automatically
 * for example (delete record, cancel reservation). Even though all the actions have Human in Loop valdation (Agent or Non Agent) still
 * in some cases there might be a need to create action but not to add that to prediction list
 *
 * 2) getActionName - this returns the name of the main function of this class , this has to be very descriptive as AI
 * will use semantic mapping in runtime to map this function
 *
 * for example some of the correct way to name the Action are
 *
 *     public String getActionName() {
 *         return "saveEmployeeDetails";
 *     }
 *
 *
 *
 * or
 *
 *     public String getActionName() {
 *  *         return "raiseCustomerComplaintTicket";
 *  *     }
 *
 *
 *
 * then you have to use the exact name for your function
 *
 * public Object raiseCustomerComplaintTicket(String customerName,String applicationName,String complaint) {
 *     ..
 *     ..
 *     ..
 *
 *     return obj;
 * }
 *
 *
 *
 * The parameters would be mapped in real time to the data coming from prompt
 * so for example if the prompt is
 *
 * "Hey this is Vishal , I was trying to reserve my car service appointment and i am getting this weird message"
 *
 * Then the Ai will trigger the raiseCustomerComplaint with data like this - customerName = Vishal, applicationName = BookingModule
 * complaint = "User cannot book service appointment"
 *
 * Return could be anything , Json Object, String or Pojo , AI will take care of mapping it
 *
 * </pre>
 */
public interface AIAction {
    String getActionName() ;

   default String getActionParameters() {return "";};
    default String getJsonRPC() {
        JSONObject json = new JSONObject();
        json.put("actionName", getActionName());
        json.put("description", getDescription());
        json.put("actionType", getActionType());
        json.put("actionGroup", getActionGroup());
        json.put("actionParameters", getActionParameters());
        return json.toString();};

    /**
     *
     * @see ActionType
     * @return ActionType
     */
    ActionType getActionType();

    /**
     * Provide a detailed description of this action name
     */
    String getDescription();

    /**
     * By default return low action risk so that it can pass without human verification
     */
    default ActionRisk getActionRisk() {
        return ActionRisk.LOW;
    }

    default String getActionGroup() {
        return "default";
    }
    default String getGroupDescription() {
        return "default";
    }


}
