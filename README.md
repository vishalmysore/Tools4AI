# Tools4AI
This project illustrates the integration of AI with enterprise tools, converting natural language prompts <br>
into actionable behaviors. By leveraging AI capabilities, it streamlines <br>
user interactions with complex systems, enhancing productivity and innovation across diverse applications.<br>
<br><br>
For example , we can integrate AI with a customer service application. Users can interact with the AI system by asking<br> 
questions or making requests in natural language. For example, a user might ask, "Schedule a maintenance <br>
appointment for my car." The AI system interprets the request, extracts relevant information such as the <br>
service required and preferred date, and then triggers the appropriate action in the customer service<br>
application to schedule the appointment. This seamless integration streamlines the process for users and<br>
enhances the efficiency of the customer service workflow.<br>
## SETUP

mvn clean install

## GCLOUD
Make sure you have gloud project set up and have enabled vertex API
```     
        String projectId = "cookgptserver"
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";
```
You have to mention your project id from cloud 


## Tools Integration
```
   AITools tools = new AITools(projectId,location,modelName);
   RestaurantPojo pojo = (RestaurantPojo)tools.invokeClass(promptText,"com.mysore.bridge.test.RestaurantPojo","RestaurantClass","Create Pojo from the prompt");
   return pojo.toString();

```
The above code can be executed with prompts like this <br> 
```
can you book a dinner reseration for Vishal and his family of 4 at Maharaj on Indian Independence day and make sure its cancellable

```
and it will create Pojo and can invoke method directly <br>
Other Tools methods are 

```
tools.invokeMethod
tools.invokeTibco 
tools.invokeDatabase
tools.invokeCustomApplication
tools.invokeSolace
tools.invokeRest

```
## Examples
``` RecipeTasteFinder  ```  This class demonstrates function calling with mapped Hashmap response <br>
``` RecipeTasteAndDiet  ```  Execute the class for function chaining with 2 functions mapped Hashmap response <br>
``` MultiBot  ```  Run this class for function chaining with 2 functions Airline booking and restaurant booking<br>
``` UdoKhaoDekho  ```  3 functions Flight , Restaurant and Movie <br>

## Advanced Examples
``` Security - Guard Rails using Spring Security``` TBD <br>
``` Application Checkout and monitoring using with Gemini - Prompt - Check if my restaurant system is up and running and able to book the reservation``` TBD <br>
``` Validation with Prompt  - Prompt - What happened the the flight booking i made whats the status?```TBD <br>

## Advanced prompt function calling
``` Can you check if my movie booking system can handle 50 reservations in 1 min ``` <br>
``` what happens if my cookgpt is giving only vegetarian recipes``` <br>