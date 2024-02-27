# Tools4AI
This project illustrates the integration of AI with enterprise tools, converting natural language prompts <br>
into <span style="font-size: larger;">**actionable behaviors**.</span> These prompts can be called <span style="font-size: larger;">**"action prompts"**</span>  
or <span style="font-size: larger;">**"actionable prompts"**</span>  By leveraging AI capabilities, it streamlines user interactions  
with complex systems, enhancing productivity and innovation across diverse applications.<br>
<br><br>
For example , we can integrate AI with a customer service application. Users can interact with the AI system by asking<br> 
questions or making requests in natural language. For example, a user might ask,**"Schedule a maintenance <br>
appointment for my car."** The AI system interprets the request, extracts relevant information such as the <br>
service required and preferred date, and then triggers the appropriate action in the customer service<br>
application to schedule the appointment. This seamless integration streamlines the process for users and<br>
enhances the efficiency of the customer service workflow.
<br>
| Prompt                                                                                                              | Action                                                                                                                                                |
|---------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| Create a <span style="color:blue">new task</span> for the marketing campaign.                                                                                                     | The AI system interprets the request and generates a new task entry within the project management tool dedicated to the marketing campaign, assigning it relevant details such as priority level, due date, and task description. |
| Generate a <span style="color:blue">sales report</span> for the previous <span style="color:blue">quarter</span>.                                                                 | The AI system accesses data from the company's sales database, analyzes the information for the previous quarter, and generates a comprehensive sales report, which is then delivered to the user or stored in the appropriate location for access. |
| Check the <span style="color:blue">inventory status</span> of <span style="color:blue">product X.</span>                                                                           | The AI system retrieves real-time inventory data for product X from the inventory management system and provides the user with information regarding current stock levels, including quantities available, locations, and any pending orders. |
| Schedule a <span style="color:blue">video conference</span> with the engineering team for next Monday at 10 AM.                                      | The AI system interfaces with the calendar and scheduling tool, creates a new event titled "Engineering Team Video Conference" for the specified date and time, and sends out meeting invitations to all members of the engineering team. |
| Submit a reimbursement request for the <span style="color:blue">business</span> trip <span style="color:blue">expenses.</span>                                                     | The AI system guides the user through the reimbursement request process, collecting necessary details such as expense receipts, dates, amounts, and purpose of expenditure. Once compiled, the system submits the reimbursement request to the appropriate department for processing. |


## SETUP

mvn clean install <br>
if you are using Intellij or eclipse make sure you set -parameters option for compiler

## GCLOUD
Make sure you have gcloud project set up and have enabled vertex API
```     
        String projectId = "cookgptserver" // this can be any server name you create in your GCloud
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";
```
You have to mention your project id from Gcloud 


## Tools Integration
```
   AITools tools = new AITools(projectId,location,modelName);
   RestaurantPojo pojo = (RestaurantPojo)tools.invokeClass(promptText,"test.com.ta.bridge.RestaurantPojo","RestaurantClass","Create Pojo from the prompt");
   return pojo.toString();

```
The above code can be executed with prompts like this <br> 
```
can you book a dinner reseration for Vishal and his family of 4 at Maharaj on Indian Independence day and make sure its cancellable

```
and it will create Pojo and can invoke method directly <br>

for example here is how a java method is mapped

```
tools.invokeMethod (prompt, JavaClassNAME, JavaMethodName)
```
The above code will map the NLP prompt to a  java method and execute the method , the argment in the method will be  
pouplated from the prompt directly so for example if you have prompt like this  
```
prompt ="give me all the Hindi movie showtimes for sunday"
```

and Map it to a JavaClass ```MovieImpl``` with Method ```getShowTime("language","day") ```
then the ```tools.invoke``` should be called with ```tools.invokeMethod(prompt,"com.package.MovieImpl","getShowTime") ```
The parameters and everything will be mapped automtically using AI and method will get invoked results will be parsed to  
AI and other tools can be chained together, in this case ```language``` will be hindi and ```day``` will be mapped to sunday    

Other Such invoke integrations are
```
tools.invokeTibco // this will create a json object and inject in Tibco based on the parameters extracted from prompts
tools.invokeDatabase //this will insert the data directly into sql db based on prompts
tools.invokeCustomApplication //parameters can be extracted from prompt and coverted into xml or json which can then be used  
                              //to call custom applicaiton for example Jira ALM etc
tools.invokeSolace
tools.invokeHTTPPOST // Call rest api based on prompt
tools.invokeHTTPGET  // call rest post method
tools.invokeMongo
```
These can be called with one single action method as well
```
tools.action(ActionType.HTTPGET, <list of arguments including prompt>)
```

or

```
tools.action(ActionType.JAVAMETHOD, <list of arguments including prompt>)

```

## Reference Examples
These are references which i have created using the above AIAction , this shows how the actionable prompts work  
``` RecipeTasteFinder  ```  This class demonstrates function calling with mapped Hashmap response <br>
``` RecipeTasteAndDiet  ```  Execute the class for function chaining with 2 functions mapped Hashmap response <br>
``` MultiBot  ```  Run this class for function chaining with 2 functions Airline booking and restaurant booking<br>
``` UdoKhaoDekho  ```  3 functions Flight , Restaurant and Movie <br>

## Advanced Reference Examples
``` Security - Guard Rails using Spring Security``` TBD <br>
``` Application Checkout and monitoring using with Gemini - Prompt - Check if my restaurant system is up and running and able to book the reservation``` TBD <br>
``` Validation with Prompt  - Prompt - What happened the the flight booking i made whats the status?```TBD <br>

## Advanced prompt function calling
``` Can you check if my movie booking system can handle 50 reservations in 1 min ``` <br>
``` what happens if my cookgpt is giving only vegetarian recipes``` <br>