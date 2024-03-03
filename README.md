# Tools4AI
Tools4AI is 100% Java implementation of Large Action Model (LAM) and can act as LLM agent for integraetion with enterprise 
Java applications.
This project illustrates the integration of AI with enterprise tools or external tools, converting natural language prompts
into <span style="font-size: larger;">**actionable behaviors**.</span> These prompts can be called <span style="font-size: larger;">**"action prompts"**</span>
or <span style="font-size: larger;">**"actionable prompts"**</span>  By leveraging AI capabilities, it streamlines user interactions
with complex systems, enhancing productivity and innovation across diverse applications.<br>

For example , we can integrate AI with a customer service application. Users can interact with the AI system by asking<br> 
questions or making requests in natural language. For example, a user might ask,**"Schedule a maintenance <br>
appointment for my car."** The AI system interprets the request, extracts relevant information such as the <br>
service required and preferred date, and then triggers the appropriate action in the customer service<br>
application to schedule the appointment. This seamless integration streamlines the process for users and<br>
enhances the efficiency of the customer service workflow.
<br>
| Prompt                                                                                                              | Action                                                                                                                                                |
|---------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| Create a <span style="color:blue">**new task**</span> for the marketing campaign.                                                                                                     | The AI system interprets the request and generates a new task entry within the project management tool dedicated to the marketing campaign, assigning it relevant details such as priority level, due date, and task description. |
| Generate a <span style="color:blue">**sales report**</span> for the previous <span style="color:blue">**quarter**</span>.                                                                 | The AI system accesses data from the company's sales database, analyzes the information for the previous quarter, and generates a comprehensive sales report, which is then delivered to the user or stored in the appropriate location for access. |
| Check the <span style="color:blue">**inventory status**</span> of <span style="color:blue">**product X.**</span>                                                                           | The AI system retrieves real-time inventory data for product X from the inventory management system and provides the user with information regarding current stock levels, including quantities available, locations, and any pending orders. |
| Schedule a <span style="color:blue">**video conference**</span> with the engineering team for next Monday at 10 AM.                                      | The AI system interfaces with the calendar and scheduling tool, creates a new event titled "Engineering Team Video Conference" for the specified date and time, and sends out meeting invitations to all members of the engineering team. |
| Submit a reimbursement request for the <span style="color:blue">**business**</span> trip <span style="color:blue">**expenses.**</span>                                                     | The AI system guides the user through the reimbursement request process, collecting necessary details such as expense receipts, dates, amounts, and purpose of expenditure. Once compiled, the system submits the reimbursement request to the appropriate department for processing. |



Prompt prediction is a technique used to anticipate user actions based on their input prompts. For instance,
if a user's prompt is "my car broke down," in addition to the action "bookTaxi," the AI system can predict a
set of subsequent actions such as "bookCarService" and "orderFood" (if it's dinner time). This predictive
capability enhances the user experience by proactively suggesting relevant actions or services based on the
context provided in the prompt.

## SETUP

Download source and build from scratch

```mvn clean install``` <br>

if you are using Intellij or eclipse make sure you set -parameters option for compiler

Or use as maven dependency 
```
<dependency>
    <groupId>io.github.vishalmysore</groupId>
    <artifactId>tools4ai</artifactId>
    <version>0.7.1</version>
</dependency>

```
check for latest version here 

```
https://repo1.maven.org/maven2/io/github/vishalmysore/tools4ai/

```


## GCLOUD
Make sure you have gcloud project set up and have enabled vertex API
```     
        String projectId = "cookgptserver" // this can be any server name you create in your GCloud
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";
```
You have to mention your project id from Gcloud 

## Quick Start
```
mvn exec:java
```
The above command will run ```WeatherSearchExample``` with a prompt ```Hey I am in Toronto do you think i can go out without jacket``` 
as we all know Gemini AI (or any other AI) does not have real time weather information, Tools4AI will pick the location information from the query 
and do an api call to https://open-meteo.com/ for real time weather infomraiton which is fed back to Gemini which gives back the answer 
<br>
If you rerun this program with a new prompt ```"Hey I am in Delhi do you think i can go out without jacket, also let me know best places to visit here"``` this
will get the weather information and feed it back to Gemini and again go back with additional question and gets back with landmarks in delhi

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

## Actionable Prompts
These are the example of actionable prompts , directly take action based on the prompts

```public class MongoAction implements AIAction {``` 
The MongoAction class implements the AIAction interface, indicating that it defines an action to be performed
within an AI system. Specifically, this class is responsible for inserting data into a MongoDB database.
Method within the MongoAction class will be automatically invoked when this action is triggered,
typically in response to specific user prompts or interactions.

## Predictable Prompts
```
@Predict 
public class MongoAction implements AIAction { 
``` 
```@Predict```Annotation will make our action predictable ,By annotating the MongoAction class with @Predict,
we are indicating that this action is predictable. This annotation instructs the AI system to automatically
call the methods within the MongoAction class when it determines that the user prompt matches the action.
In other words, if the input prompt provided by the user aligns with the behavior represented by the
MongoAction, the AI system will invoke the corresponding method within MongoAction to execute the action of 
inserting data into the MongoDB database. This predictive capability streamlines user interactions by 
automatically executing relevant actions based on user prompts.

```
@Predict
public class SendEmailAction implements AIAction {
public void sendEmail(String recipient, String message) {
// Logic to send an email to the specified recipient with the given message
}
}
```
This action is responsible for sending an email. When annotated with @Predict, the AI system will automatically
call the execute method of SendEmailAction when it predicts that the user prompt is related to sending an email.

```
@Predict
public class SearchAction implements AIAction {
    public void search(String query) {
        // Logic to perform a search with the specified query
    }
}

```
When annotated with @Predict, the AI system will call the execute method of SearchAction when it 
predicts that the user prompt is related to searching for information.

## Advanced Reference Examples
``` Security - Guard Rails using Spring Security``` TBD <br>
``` Application Checkout and monitoring using with Gemini - Prompt - Check if my restaurant system is up and running and able to book the reservation``` TBD <br>
``` Validation with Prompt  - Prompt - What happened the the flight booking i made whats the status?```TBD <br>

## Advanced prompt function calling
``` Can you check if my movie booking system can handle 50 reservations in 1 min ``` <br>
``` what happens if my cookgpt is giving only vegetarian recipes``` <br>

## Response Validation

### Hallucination

```ZeroShotHallucinationDetector``` is designed to assess the consistency of responses generated by a Large Language Model (LLM) and detect potential hallucinations. It operates by breaking down an original question into multiple granular questions, each probing different aspects or variations of the inquiry. These granular questions are then presented to the LLM separately, generating responses that are subsequently compared to the original question within its original context.

During comparison, factors such as semantic coherence, relevance, and contextual alignment are evaluated to quantify the consistency between each response and the original question. This evaluation results in a percentage score for each response, representing its level of conformity with the original query.

Finally, these individual percentage scores are aggregated to calculate a cumulative percentage. If the cumulative percentage surpasses a predefined threshold, it indicates a discrepancy or potential hallucination.

By systematically analyzing responses in this manner, the class provides a robust mechanism for assessing the reliability and coherence of LLM-generated content.

This method employs a Zero Shot approach to detect hallucination, utilizing a straightforward methodology devoid of external sources. It operates as follows:

Input: The method takes in responses generated by the Large Language Model (LLM) without relying on any additional data sources.

Granular Analysis: It breaks down the original question into multiple granular inquiries, covering diverse aspects or variations of the initial query.

Zero Shot Evaluation: Without external references, the method evaluates each response against the original question, assessing factors such as semantic coherence and contextual relevance.

Consistency Assessment: Based on the comparison, the method quantifies the consistency of each response, assigning a score indicative of its conformity with the original query.

Cumulative Evaluation: These individual scores are then aggregated to derive a cumulative assessment, providing insight into the overall coherence of the LLM-generated responses.

By employing a simple yet effective Zero Shot technique, this method offers a streamlined approach to detect potential hallucinations in LLM-generated content, contributing to the reliability and trustworthiness of AI-generated outputs.

### Bias
Fairness