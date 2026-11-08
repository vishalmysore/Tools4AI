# Tools4AI + Ollama quickstart

Type a sentence in plain English and Tools4AI runs the matching Java method, with the arguments
filled in by a model running on your own machine. No API key and no cloud account.

```text
Model  : qwen2.5:3b
Prompt : Book a table for 4 people under the name Maria this Friday
Result : Booked a table for 4 under Maria on Friday
```

## Run it

You need Java 17+, Maven, and [Ollama](https://ollama.com/download).

```bash
ollama pull qwen2.5:3b
```

```bash
mvn -q compile exec:java
```

Try your own prompt:

```bash
mvn -q compile exec:java -Dexec.args="Is pizza on the menu today?"
```

Each run takes about 10–30 seconds on a laptop CPU. The first run also downloads the Maven dependencies.

## How it works

[RestaurantAgent.java](src/main/java/quickstart/RestaurantAgent.java) is an ordinary class:

```java
@Agent(groupName = "restaurant", groupDescription = "Restaurant table bookings and menu questions")
public class RestaurantAgent {

    @Action(description = "Book a table at the restaurant")
    public String bookTable(String customerName, int numberOfPeople, String day) { ... }

    @Action(description = "Check whether a dish is on today's menu")
    public String isOnMenu(String dish) { ... }
}
```

For each prompt, Tools4AI asks the model which `@Action` fits, asks it to fill in the parameters,
then calls the method. [QuickStart.java](src/main/java/quickstart/QuickStart.java) is a single call:

```java
Object result = new OpenAiActionProcessor().processSingleAction(prompt);
```

Ollama serves an OpenAI-compatible API, so the OpenAI processor talks to it. The connection is set
in [tools4ai.properties](src/main/resources/tools4ai.properties):

```properties
openAiKey=ollama
openAiBaseURL=http://localhost:11434/v1
action.packages.to.scan=quickstart
```

## Add your own action

Add a method to `RestaurantAgent` (or a new `@Agent` class in the `quickstart` package):

```java
@Action(description = "Cancel a booking for a customer")
public String cancelBooking(String customerName) {
    return "Cancelled the booking for " + customerName;
}
```

Then run `mvn -q compile exec:java -Dexec.args="Please cancel the booking for Maria"`.

Tips:

- Give parameters descriptive names. The model sees them, so `customerName` works far better than `name1`.
  The pom compiles with `-parameters` so the real names are kept.
- Write the `description` the way a user would ask for the action.
- Avoid apostrophes in `-Dexec.args` (`Maria's`): the exec plugin treats them as quotes.
- [prompt.properties](src/main/resources/prompt.properties) words the "which action?" question so that
  small models answer with an action name. Without it, `qwen2.5:3b` sometimes replies with a number
  once an agent has three or more actions.

## Use a different model

```bash
mvn -q compile exec:java -DopenAiModelName=phi4
```

| Model | Download | Notes |
|-------|----------|-------|
| `qwen2.5:3b` | 1.9 GB | Default. Picked the right action and filled every parameter in our tests |
| `phi4` | 9.1 GB | Very reliable, but about 5x slower on a CPU |
| `qwen2.5:1.5b` | 1 GB | Too small: often skips parameters |
