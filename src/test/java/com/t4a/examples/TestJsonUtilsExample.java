package com.t4a.examples;

import com.t4a.JsonUtils;
import com.t4a.api.JavaMethodInvoker;
import com.t4a.examples.actions.PlayerWithRestaurant;
import com.t4a.examples.actions.SearchAction;
import com.t4a.predict.PredictionLoader;

import java.lang.reflect.Method;
import java.util.List;

public class TestJsonUtilsExample {
    public static void main(String[] args) throws Exception {
        JsonUtils utils = new JsonUtils();
        Method[] met = SearchAction.class.getMethods();


       /* met = CustomerWithQueryAction.class.getMethods();
        for (Method m:met
        ) {
            if(m.getName().equals("computerRepairWithDetails")){
                String jsonStr = utils.convertMethodTOJsonString(m);
                jsonStr = PredictionLoader.getInstance().getOpenAiChatModel().generate(" Here is your prompt {Vishal row is our customer he is rasing a reqest for his compture which is not working we need to fix this by Jan 1 2025} - here is the json - "+jsonStr+" - populate the fieldValue ");
                System.out.println(jsonStr);
                JavaMethodInvoker.parse(jsonStr);
            }
        }
*/
        met = new PlayerWithRestaurant().getClass().getMethods();
        for (Method m:met
        ) {
            if(m.getName().equals("notifyPlayerAndRestaurant")){
                String prm = "sachin tendular is a cricket player and he has played 400 matches, his max score is 1000, he wants to go to " +
                        "Maharaja restaurant in toronto with 4 of his friends on Indian Independence Day, can you notify him and the restarurant";
                String jsonStr = utils.convertMethodTOJsonString(m);
                jsonStr = PredictionLoader.getInstance().getOpenAiChatModel().generate(" Here is your prompt {"+prm+"} - here is the json - "+jsonStr+" - populate the fieldValue ");
                System.out.println(jsonStr);
                JavaMethodInvoker invoke = new JavaMethodInvoker();
                Object obj [] = invoke.parse(jsonStr);
                List<Object> parameterValues = (List<Object>) obj[1];
                List<Class<?>> parameterTypes = (List<Class<?>>) obj [0];
                Class<?> clazz = Class.forName(PlayerWithRestaurant.class.getName()); // Replace "YourClassName" with the actual class name containing the method
                Method method = clazz.getMethod("notifyPlayerAndRestaurant", parameterTypes.toArray(new Class<?>[0]));
                Object instance = clazz.getDeclaredConstructor().newInstance();
                Object result = method.invoke(instance, parameterValues.toArray());
                System.out.println(result);
            }
        }


    }


}
