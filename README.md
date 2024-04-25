<div align="center">
  <a href="https://www.linkedin.com/posts/vishalrow_ai-appdevelopment-actions-activity-7171302152101900288-64qg?utm_source=share&utm_medium=member_desktop">
    <img src="tools4ai.png"  width="300" height="300">
  </a>
</div>
<p align="center">
    <img  src="https://api.visitorbadge.io/api/visitors?path=https%3A%2F%2Fgithub.com%2Fvishalmysore%2Ftools4ai&countColor=black&style=flat%22">
    <a target="_blank" href="https://github.com/vishalmyore/tools4ai"><img src="https://img.shields.io/github/stars/vishalmysore/tools4ai?color=black" /></a>   
</p>

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=vishalmysore_Tools4AI)](https://sonarcloud.io/summary/new_code?id=vishalmysore_Tools4AI) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=vishalmysore_Tools4AI&metric=bugs)](https://sonarcloud.io/summary/new_code?id=vishalmysore_Tools4AI)  

### Table of Contents
- [Rapid Start](#-Rapid-Start)
- [Tools4AI](#-Tools4AI)
- [SetUp](#setup)  
  - [Gemini](#gemini)
  - [OpenAI](#OpenAi)
  - [Anthropic](#anthropic)  
- [Reference Examples](#%EF%B8%8F-reference-examples)  
  - [Java Actions](#java-actions)
  - [HTTP REST Actions](#http-actions-swagger)
  - [Shell Actions](#shell-actions)
  - [Custom HTTP Actions](#custom-http-actions)
  - [Selenium Integration](#selenium-integration)
  - [Image Actions](#image-actions)
  - [Automated UI validation](#automated-ui-validations)
  - [Spring Integration](#spring-integration)
  - [JavaDocs](#-javadocs)
- [Prediction Loaders](#-prediction-loaders)  
  - [Java Prediction Loader](#-java-prediction-loaders)
  - [Swagger Prediction Loader](#%EF%B8%8F-swagger-prediction-loader)
  - [Shell Prediction Loader](#%EF%B8%8F-shell-prediction-loader)
  - [Extended Prediction Loader](#%EF%B8%8F-extended-prediction-loader)  
- [Response Validation](#response-validation)
  - [Hallucination](#hallucination)   
- [Autonomous Agent](#autonomous-agent)  
  - [Action Scripts](#action-script)


# 📌 Rapid Start
🧱 Do you want to start building ASAP , Look at Rapid start here https://github.com/vishalmysore/simplelam  

🌱 Integration of Spring Controller and AI Actions - https://github.com/vishalmysore/SpringActions  

👉 Live Demo - https://huggingface.co/spaces/VishalMysore/EnterpriseAIHub


# 💡 Tools4AI
Tools4AI is 100% Java implementation of Large Action Model (LAM) and can act as LLM agent for integration with enterprise 
Java applications.
This project illustrates the integration of AI with enterprise tools or external tools, converting natural language prompts
into <span style="font-size: larger;">**actiona ble behaviors**.</span> These prompts can be called <span style="font-size: larger;">**"action prompts"**</span>
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

# SetUp

Download source and build from scratch

```mvn clean install``` <br>

if you are using Intellij or eclipse make sure you set -parameters option for compiler

Or use as maven dependency 
```
<dependency>
    <groupId>io.github.vishalmysore</groupId>
    <artifactId>tools4ai</artifactId>
    <version>0.9.6</version>
</dependency>

```
check for latest version here  https://repo1.maven.org/maven2/io/github/vishalmysore/tools4ai/

## Gemini
Make sure you have gcloud project set up and have enabled vertex API
```     
        String projectId = "cookgptserver" // this can be any server name you create in your GCloud
        String location = "us-central1";
        String modelName = "gemini-1.0-pro";
```
You have to mention your project id from Gcloud 

## OpenAI

If you plan to use OpenAI instead of Gemini you will need OpenAI api key
Instructions here https://help.openai.com/en/articles/4936850-where-do-i-find-my-openai-api-key

## Anthropic

If you plan to use Anthropic you will need anthropic api key https://docs.anthropic.com/claude/reference/getting-started-with-the-api


# ✈️ Reference Examples
  
[OpenAI](src/test/java/com/t4a/test/OpenAIActionTest.java)
[Gemini](src/test/java/com/t4a/test/ActionTest.java)  
[Anthropic](src/test/java/com/t4a/test/AnthropicActionTest.java)  
[Gemini1.5Pro](src/test/java/com/t4a/test/AnthropicActionTest.java)  


## Java Actions

Fastest way to create action is by writing a java class  and  annotate its method with ```@Action```
annotation.

**Java Actions:** These are Java classes that have methods with ```@Action``` annotation . They are designed to 
perform specific actions based on prompts processed through Tools4AI.  

**@Predict Annotation:** This is a <u>class level</U> annotation. This annotation is used to add the action to the prediciton list, doing this will make sure
the action is called automatically when the prompt is processed and if the prompt matches with the action (action name or method name). All the methods 
in this class annotated with @Action will be added to the prediction list.

**@Action Annotation:** This is a <u>method level</u> annotation. The action method within the Java class is annotated with @Action to specify the action's behaviour
this is the actual method which will be triggered if the prompt matches by AI. You can have as many methods as you want with @Action
annotation in the class.

**Method Execution:** When a prompt like "My friend's name is Vishal, I don’t know what to cook for him today." 
is processed, the ```whatFoodDoesThisPersonLike``` method is automatically called with "Vishal" as the argument, 
and it returns "Paneer Butter Masala" as Vishal's preference. 
``` 
@Predict
public class SimpleAction  {

    @Action( description = "what is the food preference of this person")
    public String whatFoodDoesThisPersonLike(String name) {
        if("vishal".equalsIgnoreCase(name))
            return "Paneer Butter Masala";
        else if ("vinod".equalsIgnoreCase(name)) {
            return "aloo kofta";
        }else
            return "something yummy";
    }

}
```
This class can contain any number of methods both public or private. All the methods with ```@Action``` annotation  
in this class are going be added to the prediction list. In this case it is just ```whatFoodDoesThisPersonLike```.

This method can contain any number of parameters (Objects, Pojos, Arrays etc) and can be of have any return value, everythign will 
be mapped at runtime using prompt. ```@Action``` annotation also takes additional values for ActionRisk and Description.

When you send a prompt to  action processor (OpenAIActionProcessor, GeminiActionProcessor or AnthoripicActionProcessor)
``` 
 String cookPromptSingleText = "My friends name is Vishal ," +
                "I dont know what to cook for him today.";
 OpenAiActionProcessor tra = new OpenAiActionProcessor();
 String functionResponse = (String)tra.processSingleAction(cookPromptSingleText);
 //functionResponse will be Paneer Butter Masala
 String aiResponse = tra.query(cookPromptSingleText,functionResponse);
 //aiResponse will be well forumulated response something like "Hey looks like your friend likes Panner Butter Masala
```

The above action will be called with ``` name = Vishal``` automatically based on the action name and prompt type.
AI will figure out that this is the correct action to call. You can also add grouping information in Predict annotation to 
make it even more targeted. You don't have to specify the Action explicitly if its not a High Risk action

**Convert prompt to Pojo**

You can convert any Prompt into a Java Pojo object. Your pojo can be simple or complex , it can have arrays, list
maps and other pojos and they all will be mapped based on the prompt

```
OpenAIPromptTransformer tra = new OpenAIPromptTransformer();
String promptText = "Mhahrukh Khan works for MovieHits inc and his salary is $ 100  he joined Toronto on 
Labor day, his tasks are acting and dancing. He also works out of Montreal and Bombay. Krithik roshan is 
another employee of same company based in Chennai his taks are jumping and Gym he joined on Indian Independce
 Day";
Organization org = (Organization) tra.transformIntoPojo(promptText, Organization.class.getName(),"","");
Assertions.assertTrue(org.getEm().get(0).getName().contains("Mhahrukh"));
Assertions.assertTrue(org.getEm().get(1).getName().contains("Krithik"));
```
The above code will map the prompt and convert into [Organization](src/test/java/com/t4a/examples/pojo/Organization.java) Pojo object 

**Trigger Action**

Trigger action based on prompt, in case its MyDiary action [MyDiaryAction](src/test/java/com/t4a/examples/actions/MyDiaryAction.java)

```
 OpenAiActionProcessor tra = new OpenAiActionProcessor();
 String promptText = "I have dentist appointment on 3rd July, then i have Gym appointment on 7th August 
 and I am meeting famous Bollywood actor Shahrukh Khan on 19 Sep. My friends Rahul, Dhawal, Aravind are 
 coming with me. My employee Jhonny Napper is comign with me he joined on Indian Independce day.
 My customer name is Sumitabh Bacchan he wants to learn acting form me he joined on labor day";
 MyDiaryAction action = new MyDiaryAction();
 MyDiary dict = (MyDiary) tra.processSingleAction(promptText,action);
 log.info(dict.toString()); 
```

If you do not specify any action then it will be predicted based on prompt and groups for example

```
MyDiary dict = (MyDiary) tra.processSingleAction(promptText)
```
As you can notice we are not passing any action with the prompt the AI will figure out the action correctly

A simple Java action can be written like this 

```
@Predict(groupName = "buildMyDiary" , groupDescription = "This is my diary details")
public class MyDiaryAction implements JavaMethodAction {
    @Action  
    public MyDiary buildMyDiary(MyDiary diary) {
        //take whatever action you want to take
        return diary;
    }
} 
```
Here the actionName is ```buildMyDiary```, MyDiary pojo will be created automatically based on prompt

**Action groups**

```
@Predict(groupName = "personal", groupDescription = "all personal actions are here") 
```
Actions have to be annontated with @Predict to be added to prediction list , they can be grouped together with
the groupName.

**Custom Pojo**

There are different annontations which be used for special Pojo mapping

**Mapping list**

```
    @ListType(Employee.class)
    List<Employee> em;
    @ListType(String.class)
    List<String> locations; 
```

**Mapping Maps in objects**
``` 
@Predict(actionName = "addSports",description = "add new Sports into the map")
public class MapAction implements JavaMethodAction {

    public Map<Integer,String> addSports(@MapKeyType(Integer.class)  @MapValueType(String.class) Map<Integer,String> mapOfSportsName) {

        return mapOfSportsName;
    }
}

```

**Special Instructions**

``` 
@Prompt(describe = "convert this to Hindi")
private String reasonForCalling;
```

The above instruction will fetch the reason for calling from the user prompt and convert it into Hindi and put
it inside the ```reasonForCalling``` String

**Ignore Field**
```
    @Prompt(ignore = true)
    private String location;
```
If you do not want to populate a field you can annotate it as ignore

**Format Date**

```
    @Prompt(dateFormat = "yyyy-MM-dd" ,describe = "if you dont find date provide todays date in fieldValue")
    private Date dateJoined;
```
The above will fetch the dateJoined from the prompt and convert it into the format. So if your prompt is
"Book my dinner reservation on Indian Independence day" , the dateJoined will be 2024-08-15

**Muliple Special Prompts**
```
 public class MyTranslatePojo {
    @Prompt(describe = "translate to Hindi")
    String answerInHindi;
    @Prompt(describe = "translate to Punjabi")
    String answerInPunJabi;

    @Prompt(describe = "translate to Tamil")
    String answerInTamil;
}
```
The above Pojo can be used to translate the prompt in multiple language and populate the result in variables

``` 
OpenAIPromptTransformer tra = new OpenAIPromptTransformer();
String promptTxt = "paneer is so good";
MyTranslatePojo myp = (MyTranslatePojo)tra.transformIntoPojo(promptTxt,MyTranslatePojo.class.getName());
System.out.println(myp);
```

Result will look something like this 

``` MyTranslatePojo(answerInHindi=पनीर इतना अच्छा है, answerInPunJabi=ਪਨੀਰ ਬਹੁਤ ਵਧੀਆ ਹੈ, answerInTamil=பனீர் எப்படி நல்லது) ```

**High Risk Actions**  
There might be some actions which you do not want to be triggered automatically but passed explicitly in the processor
such actions can be annotated with HighRisk

```
@Predict(actionName = "restartTheECOMServer",description = "will be used to restart the server" , 
riskLevel = ActionRisk.HIGH, groupName = "customer support", 
groupDescription = "actions related to customer support")
public class ServerRestartAction implements JavaMethodAction {
    public String restartTheECOMServer(String reasonForRestart, String requestedBy) {
        return " Server has been restarted by "+requestedBy+" due to following reason "+reasonForRestart;
    }
}

```
The above action is marked as High Risk action so if you try to call it with simple prompt

``` 
OpenAiActionProcessor processor = new OpenAiActionProcessor()
String restartPrompt = "Hey I am Vishal , restart the server as its very slow ";
String functionResponse = (String)processor.processSingleAction("restartPrompt");

```
It will not be triggered even though the AI will correct identify which action to trigger still it will get blocked.

You will have to call it explicitly this way

``` 
OpenAiActionProcessor processor = new OpenAiActionProcessor()
String restartPrompt = "Hey I am Vishal , restart the server as its very slow ";
ServerRestartAction restartAction = new ServerRestartAction();
String functionResponse = (String)processor.processSingleAction("restartPrompt", restartAction);
```


## HTTP Actions (Swagger)

Any application exposing HTTP REST API can be converted into actions for example here is my sample [swagger_actions.json.](https://huggingface.co/spaces/VishalMysore/EnterpriseAIHub/blob/main/swagger_actions.json)
All the REST calls such as get, post, put, delete will be mapped to actions and based on the prompts they can be triggered
automatically

``` 
{
  "endpoints" : [
    {
      "swaggerurl": "https://fakerestapi.azurewebsites.net/swagger/v1/swagger.json",
      "group": "Books Author Activity",
      "description": "This is for all the actions related books , Authors, photos and users trying to read books and view photos",
      "baseurl": "https://fakerestapi.azurewebsites.net/",
      "id": "fakerestapi"
    },
    {
      "swaggerurl": "https://petstore3.swagger.io/api/v3/openapi.json",
      "baseurl": "https://petstore3.swagger.io/",
      "group": "Petstore API",
      "description": "This is for all the actions related to pets",
      "id": "petstore"
    } ,
    {
      "swaggerurl": "https://vishalmysore-instaservice.hf.space/v3/api-docs",
      "baseurl": "https://vishalmysore-instaservice.hf.space/",
      "group": "Enterprise Support and Tickeing System",
      "description": "This action is to create tickets track bugs across the enterprise",
      "id": "InstaService"
    }      
  ]
}
```

Books related api are put in a group called Books Author Activity group, similarly Petstore API is group for all the rest calls exposed by Petstore app 
if you provide a prompt "create a ticket for me with number 1 and issue is compture not working" 
it will automatically create a ticket on InstaService you can view the logs here https://huggingface.co/spaces/VishalMysore/InstaService?logs=container

```
@Test
public void testHttpActionOpenAI() throws AIProcessingException, IOException {
  OpenAiActionProcessor processor = new OpenAiActionProcessor();
  String postABook = "post a book harry poster with id 189 the publish date is 2024-03-22 and the description 
  is about harry who likes poster its around 500 pages  ";
  String result = (String)processor.processSingleAction(postABook);
  Assertions.assertNotNull(result);
  String success = TestHelperOpenAI.getInstance().sendMessage("Look at this message - "+result+" -
   was it a success? - Reply in true or false only");
  log.debug(success);
  Assertions.assertTrue("True".equalsIgnoreCase(success));

} 
```
This will automatically trigger HTTP post call with correct parameters 

Read the complete link on how this has been deployed [here](https://www.linkedin.com/pulse/enterprise-ai-hub-llm-agent-built-openai-java-vishal-mysore-0p7oc/?trackingId=iZoQDW3%2BTH6j0%2FkbEMUxFw%3D%3D)


## Shell Actions 

Any kind of script can be coverted into Action for function calling by configuring the script in [shell_actions.yml](src/test/resources/shell_actions.yaml)
```
groups:
  - name: Employee Actions
    description : This is actions for all the new employees
    scripts:
      - scriptName: "test_script.cmd"
        actionName: saveEmployeeInformation
        parameters: employeeName,employeeLocation
        description: This is a command which will save employee information
		
```

Here we are creating a group called Employee Actions and adding an action called ``` saveEmployeeInformation``` into the group. The parameters it takes are ```employeeName ``` and ```employeeLocation ```
Calling and actionprocessor with these kinds of prompt will trigger this Action
``` 
 OpenAiActionProcessor tra = new OpenAiActionProcessor();
 String promptText = "A new employee joined today in Toronto. Her name is Madhuri Khanna"; 
 tra.processSingleAction(promptText);
 
```
		
As You can notice we are not passing the action explicitly , it will be predicted by the AI at runtime and triggered.


## Custom HTTP Actions

If you do not have swagger URL and would like to configure HTTP rest end points it can be done via Custom HTTP Actions by configuring them in [http_actions.json](src/test/resources/http_actions.json)


``` 
{
  "endpoints": [
    {
      "actionName": "getUserDetails",
      "description" : " this will fetch User details from the user inventory corporate application",
      "url": "https://api.example.com/users/",
      "type": "GET",
      "input_object": [
      {
        "name": "userId",
        "type": "path_parameter",
        "description": "User ID"
      }
      ],

      "output_object": {
        "type": "json",
        "description": "User object"
      },
      "auth_interface": {
        "type": "Bearer Token",
        "description": "Authentication token required"
      }
    },
    {
      "actionName": "somethingNotVeryUseful",
      "url": "https://api.example.com/temperature",
      "description" : " this will get real time temperature from the weather api",
      "type": "GET",
      "input_object":[
        {
          "name": "locationId",
          "type": "query_parameter",
          "description": "Location ID"
      }
      ],
      "output_object": {
        "type": "json",
        "description": "Real-time temperature data"
      },
      "auth_interface": {
        "type": "API Key",
        "description": "API key required"
      }
    }
  ]
}

```
## Image Actions
Tools4AI uses Gemini (gemini-1.0-pro-vision) to enhance AI capabilities by enabling the system to analyze images
and automatically execute relevant actions based on the visual data it processes. This development is 
particularly crucial in emergency management, where speed and accuracy of response can save lives and property.

Reference code is [here](https://github.com/vishalmysore/sam/blob/main/src/main/java/org/example/image/ImageActionExample.java)

Detailed article on the same is avaiable [here](https://medium.com/@visrow/image-recognition-and-function-calling-with-gemini-and-java-e28b0356d3de)
```  
package org.example.image;

import com.t4a.processor.AIProcessingException;
import com.t4a.processor.GeminiImageActionProcessor;
import com.t4a.processor.GeminiV2ActionProcessor;

public class ImageActionExample {
    public static void main(String[] args) throws AIProcessingException {
        GeminiImageActionProcessor processor = new GeminiImageActionProcessor();
        String imageDisription = processor.imageToText(args[0]);
        GeminiV2ActionProcessor actionProcessor = new GeminiV2ActionProcessor();
        Object obj = actionProcessor.processSingleAction(imageDisription);
        String str  = actionProcessor.summarize(imageDisription+obj.toString());
        System.out.println(str);
    }
}
```
 <img src="accident.png"  width="300" height="300">

If you execute the [ImageActionExample](https://github.com/vishalmysore/sam/blob/main/src/main/java/org/example/image/ImageActionExample.java) 
with above image as source it correctly identifies that we need to call Ambulance 

``` 
The image depicts a car accident involving a blue car and a red car on a city street. The blue car has front-end damage while the red car has rear-end damage. Debris from the accident is scattered on the street and a 
police officer is present at the scene. An ambulance has been called and is seen in the background.
```

Direct Action from Visual Cues: Whether it's a surveillance image of a car accident or a live feed of a residential fire, Tools4AI can immediately recognize critical situations and initiate appropriate emergency protocols without human input.
A sample action is written and the code is available [here](https://github.com/vishalmysore/sam/blob/main/src/main/java/org/example/image/action/EmergencyAction.java)
``` 
@Predict(actionName = "callEmergencyServices", description = "This action will be called in case of emergency", groupName = "emergency")
public class EmergencyAction implements JavaMethodAction {
    public String callEmergencyServices(@Prompt(describe = "Ambulance, Fire or Police") String typeOfEmergency) {
        return typeOfEmergency+" has been called";
    }
} 
```
## Automated UI Validations

Images can also be converted into Json and Pojos for UI based validations. You can use selenium with Tools4AI 
to validated your UI instead of using elements from the web page , more details [here](https://medium.com/@visrow/selenium-and-ai-ui-validations-with-ai-1799ab2f305e)  

 <img src="auto.PNG"  width="300" height="300">

The above image can be converted into a Pojo object with help of Tools4AI

```  
WebDriverManager.chromedriver().setup();

ChromeOptions options = new ChromeOptions();
options.addArguments("--headless");  // Setting headless mode
ptions.addArguments("--disable-gpu");  // GPU hardware acceleration isn't useful in headless mode
options.addArguments("--window-size=1920,1080");  // Set the window size
WebDriver driver = new ChromeDriver(options);

driver.get("https://google.com");
// Your code to interact with the page and take screenshots
// Take screenshot and save it as file or use as bytes
TakesScreenshot ts = (TakesScreenshot) driver;
byte[] screenshotBytes = ts.getScreenshotAs(OutputType.BYTES);
GeminiImageActionProcessor imageActionProcessor = new GeminiImageActionProcessor();
imageActionProcessor.imageToText(screenshotBytes)
//File srcFile = ts.getScreenshotAs(OutputType.FILE);
//File destFile = new File("screenshot.png");
//FileHandler.copy(srcFile, destFile);
driver.quit();
```

The pojo it will convert to is 

``` 
import lombok.*;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AutoRepairScreen {
    double fullInspectionValue;
    double tireRotationValue;
    double oilChangeValue;
    Integer phoneNumber;
    String email;
    String[] customerReviews;
}
```

Integrating Tools4AI with Selenium offers a revolutionary approach to UI validation, streamlining the testing 
process by validating UI elements in their entirety. Rather than the traditional method of scrutinizing each 
element individually, this integration enables comprehensive, automated verification of tags and elements.
It simplifies the UI Validation workflow, ensuring accuracy and efficiency in confirming the UI's adherence to design specifications  

## Selenium integration
Tools4AI's integration with Selenium introduces a flexible way to automate UI testing. Instead of traditional
Java code for Selenium scripts, Tools4AI allows you to define test scenarios in plain English, offering a more 
accessible approach to testing web applications. These English-based commands can be converted into Selenium 
code to automate web-based interactions and streamline testing.  

**Example of Selenium Test with Tools4AI**
``` 
 WebDriver driver = new ChromeDriver(options);
 SeleniumProcessor processor = new SeleniumProcessor(driver);
 processor.processWebAction("go to website https://the-internet.herokuapp.com");
 boolean buttonPresent =  processor.trueFalseQuery("do you see Add/Remove Elements?");
 if(buttonPresent) {
    processor.processWebAction("click on Add/Remove Elements");
    // perform other function in simple english
 } //else {
   // processor.processSingleAction("Create Jira by taking screenshot");
  // }
 processor.processWebAction("go to website https://the-internet.herokuapp.com");
 boolean isCheckboxPresent =  processor.trueFalseQuery("do you see Checkboxes?");
 if(isCheckboxPresent) {
   processor.processWebAction("click on Checkboxes");
   processor.processWebAction("select checkbox 1");
 }
```
In this example, the ```SeleniumProcessor``` processes commands in plain English and converts them into Selenium 
actions. This approach allows for complex interactions without manually writing Java code for each test. 
Tools4AI serves as a bridge between natural language and Selenium, making it easier to automate UI testing in
a way that is both efficient and intuitive.  

This integration offers substantial benefits for teams looking to streamline their UI validation process.
By enabling a more straightforward way to define and execute Selenium scripts, Tools4AI provides a flexible
framework for automating Selenium-based tests.  

## Spring integration

All the action processors have Spring integration as well 

``` 
SpringAnthropicProcessor springAnthropic = new SpringAnthropicProcessor(applicationContext)
```
``` 
SpringGeminiProcessor springGemini = new SpringGeminiProcessor();
```

```  
SpringOpenAIProcessor springOpenAI = new SpringOpenAIProcessor();
```

You can use this for spring injection and it works exactly as all other action processors , only difference is
that instead of creating new action beans it will reuse the beans already created by spring


look at the example here https://github.com/vishalmysore/SpringActions



## 📘 JavaDocs

Look at the java docs here - https://javadoc.io/doc/io.github.vishalmysore/tools4ai/latest/index.html


## 🧱 Prediction Loaders

### 🔑 Java Prediction Loaders

All the classes implementing ```JavaMethodAction``` interfaces and having annotation ```@Predict``` are added to prediction list
```JavaMethodAction``` is integral to creating all AI-related actions, with each action implemented as a function adhering to the principles of functional programming. The function's name should be descriptive, aligning closely with the action it performs
```@Predict``` Annotation: This annotation ensures that the AIAction object is included in our prediction list. While not mandatory, it's advisable to mark all actions with @Predict for automatic execution. However, for highly customized actions like deleting records or canceling reservations, omitting this annotation might be preferable to prevent automatic execution.

```actionName``` the descriptive name of the primary function within the class. It's crucial to name this function accurately, as AI utilizes semantic mapping at runtime to correlate the function.

```
@Predict(actionName = "whatFoodDoesThisPersonLike", description = "what is the food preference of this person ")
public class SimpleAction implements JavaMethodAction {

    public String whatFoodDoesThisPersonLike(String name) {
        if("vishal".equalsIgnoreCase(name))
            return "Paneer Butter Masala";
        else if ("vinod".equalsIgnoreCase(name)) {
            return "aloo kofta";
        }else
            return "something yummy";
    }

}

```

So prompt like ```Hey Vishal is coming to my house for dinner``` will automatically trigger method ```whatFoodDoesThisPersonLike``` with ```name``` Vishal

### 🖌️ Shell Prediction Loader  

The prediction loader is responsible for loading command scripts, shell scripts, Python scripts, or any other
type of script from configuration files. It utilizes the actionName field from the configuration to map to 
prompts in real-time. Here's an example configuration entry: 

```
- scriptName: "test_script.cmd"
  actionName: saveEmployeeInformation
  parameters: employeeName,employeeLocation
  description: This is a command which will save employee information
 ```
During runtime, the prediction loader dynamically extracts parameters from the prompt. Subsequently, it invokes 
the corresponding script based on the action name. Upon execution, the script processes the parameters and 
generates a result, which is then sent back to the AI system.  Finally, the AI system formulates a response based on the received result and provides feedback accordingly.
User: "Hey, Bahubali joined the IFC and we are so happy."

In this prompt:

"Hey" serves as a casual greeting.
"Bahubali" represents the name of the new joiner, which needs to be extracted as a parameter.
"joined the IFC" implies an action, where the specifics of the action need to be determined. "IFC" is the name of the organization.
"we are so happy" provides additional context but doesn't directly affect the action to be taken.
The AI system first matches the user's intent with a list of all available actions. In this case, it selects the "saveEmployeeInformation" action as the best match. Then, it maps the parameters accordingly: "Bahubali" as the employee's name and "IFC" as the organization's name. This allows the AI system to accurately understand and execute the user's request.

### ✒️ Swagger Prediction Loader  
<img src="restapi.png" >

The Swagger Prediction Loader is capable of directly loading HTTP endpoints as predictions, enabling automatic
execution of commands that semantically match the endpoints with extracted parameters. The screenshot provided
is from https://fakerestapi.azurewebsites.net/index.html, included as an example. Each endpoint within this 
API is converted to an ```HttpPredictedAction``` and dynamically added to the prediction list in real-time by the 
```SwaggerPredictionLoader.``` 

This seamless integration allows for streamlined execution of commands based on the available HTTP endpoints.
**Parsing Swagger/OpenAPI Specification:** The Swagger Prediction Loader reads the Swagger/OpenAPI specification file, which describes the available endpoints, their methods (e.g., GET, POST), parameters, and other details.

**Endpoint Extraction:** The loader extracts each endpoint from the specification, along with its associated metadata such as method, path, parameters, etc.

**Action Mapping:** For each endpoint, the loader creates an HttpPredictedAction object. This action represents the corresponding HTTP operation (e.g., GET, POST) that clients can perform on the endpoint.

**Parameter Extraction:** The loader extracts parameters defined for each endpoint, such as query parameters, path parameters, headers, etc.

**Action Configuration:** The extracted parameters are configured within the HttpPredictedAction object, allowing for dynamic parameterization during execution. Parameters may be mapped to placeholders within the endpoint URL or included in the request body, headers, etc., as specified by the endpoint definition.

**Addition to Prediction List:** Finally, the HttpPredictedAction objects are added to the prediction list, making them available for automatic execution based on user prompts. Users can invoke actions by providing prompts that match the semantic intent of the mapped endpoints, and the system will execute the corresponding HTTP operation with the extracted parameters.

In essence, the Swagger Prediction Loader leverages the structure and metadata defined in the Swagger/OpenAPI
specification to dynamically create HttpPredictedAction objects, allowing for seamless integration of HTTP
endpoints into the prediction system

### ✍️ Http Prediction Loader

```HttpPredictionLoader``` is responsible for loading the manual http endpoint configuration which look something like this 

```{
  "endpoints": [
    {
      "actionName": "getUserDetails",
      "description" : " this will fetch User details from the user inventory corporate application",
      "url": "https://api.example.com/users/",
      "type": "GET",
      "input_object": [
      {
        "name": "userId",
        "type": "path_parameter",
        "description": "User ID"
      }
      ],

      "output_object": {
        "type": "json",
        "description": "User object"
      },
      "auth_interface": {
        "type": "Bearer Token",
        "description": "Authentication token required"
      }
    },
```
For the manual definition of HTTP endpoints using a configuration file like the one provided, the process involves specifying each endpoint along with its associated details such as action name, description, URL, HTTP method (type), input parameters, output object, and authentication interface. Here's how the mapping process occurs:

**Configuration File Parsing:** The application parses the configuration file to extract each endpoint definition along with its metadata.

**Endpoint Mapping:** For each endpoint defined in the configuration file, an HttpPredictedAction object is created to represent the corresponding HTTP operation.

**Action Configuration:** The metadata provided in the configuration file is used to configure the HttpPredictedAction object:

**Action Name:** Specifies the name of the action, which serves as a unique identifier.  
**Description:** Provides a brief description of what the action does or its purpose.  
**URL:** Defines the endpoint URL to which the HTTP request will be sent.  
**HTTP Method (Type):** Specifies the HTTP method (e.g., GET, POST) to be used for the request.  
**Input Parameters:** Describes the input parameters required for the HTTP request, such as path parameters, query parameters, etc.  
**Output Object:** Defines the format and structure of the response expected from the endpoint.  
**Authentication Interface:** Specifies the authentication mechanism required to access the endpoint, along with any necessary credentials.  
**Parameter Extraction:** The input parameters defined for each endpoint are extracted and configured within the HttpPredictedAction object.  

**Addition to Prediction List:** Finally, the HttpPredictedAction objects representing the manually defined endpoints are added to the prediction list, making them available for automatic execution based on user prompts.

This approach allows for flexibility in defining HTTP endpoints outside of a Swagger/OpenAPI specification, enabling the manual configuration of endpoints to suit specific application requirements.

### 🖊️ Extended Prediction Loader
The ```ExtendedPredictionLoader``` offers a mechanism for creating custom prediction loaders. While Shell, HTTP, and Java Methods are supported by default, there may arise situations or use cases necessitating a custom set of actions. It's important to note the distinction between custom actions and ```ExtendedPredictedAction.``` Custom actions can be created by implementing the AIAction class, while ExtendedPredictedAction have their own loading mechanism. These actions are already present in the prediction list by default and cannot be predicted again.

To create custom implementations of ExtendedPredictionLoader, you need to annotate the loader class with ```@ActivateLoader.``` Prediction loader will then identify all classes with this annotation and call the ```getExtendedActions()``` method. This method should return the action names along with their corresponding ExtendedPredictOptions, allowing for the seamless integration of custom actions into the prediction system.

## Autonomous Agent 

### Action Script
If you have a complete script written in English , ScriptProcessor will process the script and provide consolidated results

```
 ScriptProcessor script = new ScriptProcessor();
 ScriptResult result =  script.process("complexTest.action");
 String resultsString = script.summarize(result)
 log.info(resultsString)

```

Sample script is here

``` 
can you reserve the flight for Vishal from Toronto to Bangalore for 3 Days on 7th december
If flight booking is successful, can you reserve the car for Vishal from Bangalore to Toronto for 10 Days on 17th december
if car booking is successful and flight cost are less than $1000 then book the sight seeing attraction called 5 star palace
if car booking is successful and flight cost are more than $1000 then book the sight seeing attraction called peanut palace
```

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
### Fairness



## Advanced Reference Examples

This will do a google search and return the result can be combined with multiaction  
```
ActionProcessor processor = new ActionProcessor();
String news = (String)processor.processSingleAction("can you search the web for Indian news");

```
Guard Rails with Spring security 
``` Security - Guard Rails using Spring Security``` TBD <br>
``` Application Checkout and monitoring using with Gemini - Prompt - Check if my restaurant system is up and running and able to book the reservation``` TBD <br>
``` Validation with Prompt  - Prompt - What happened the the flight booking i made whats the status?```TBD <br>

## 🧾 Advanced prompt function calling
``` Can you check if my movie booking system can handle 50 reservations in 1 min ``` <br>
``` what happens if my cookgpt is giving only vegetarian recipes``` <br>