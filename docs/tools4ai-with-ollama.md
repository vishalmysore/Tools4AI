# Build a Local AI Agent in Java with Ollama: Call Your Java Methods from Plain English (No API Key)

Most AI agent tutorials are written for Python. If you are a Java developer, you have probably watched
LangChain demos and wondered how to do the same thing in the language your production systems actually
run on, without rewriting them, without paying for an API key, and without sending customer data to
the cloud.

This guide shows you how. In about 10 minutes you will build a **Java AI agent** that:

- understands a plain English request such as *"Book a table for 4 people under the name Maria this Friday"*,
- decides which of your Java methods should handle it,
- pulls the arguments out of the sentence (`customerName = "Maria"`, `numberOfPeople = 4`, `day = "Friday"`),
- and calls the method, with real Java types.

It all runs on your own laptop with a free, open-source **local LLM** through
[Ollama](https://ollama.com). No GPU, no cloud account, no OpenAI key, and no data leaves your machine.

## Why this is harder in Java than it should be

The technique behind every AI agent is called **function calling** (or **tool calling**): you describe
your functions to a large language model, it picks one and fills in the arguments, and your code runs it.

Doing that by hand in Java means a lot of plumbing:

1. Write a JSON schema describing every method and every parameter.
2. Send the schemas and the user's request to the model.
3. Parse the tool call that comes back.
4. Convert the string arguments into `int`, `boolean`, dates and your own objects.
5. Find the right method and invoke it.
6. Repeat all of it every time you add or change a method.

That is a lot of code that has nothing to do with your business logic.

## The shortcut: annotate your Java methods

[Tools4AI](https://github.com/vishalmysore/Tools4AI) is a free, open-source (MIT) Java library that does
all six steps for you. You add two annotations, `@Agent` on a class and `@Action` on each method, and it
handles the schemas, the prompts, the type conversion and the method call. It works with OpenAI, Anthropic
Claude, Google Gemini, and any server that speaks the OpenAI API. Ollama is one of those, which is how it
runs local models.

Here is the whole idea in one class:

```java
@Agent(groupName = "restaurant", groupDescription = "Restaurant table bookings and menu questions")
public class RestaurantAgent {

    @Action(description = "Book a table at the restaurant")
    public String bookTable(String customerName, int numberOfPeople, String day) {
        return "Booked a table for " + numberOfPeople + " under " + customerName + " on " + day;
    }
}
```

and one line to use it:

```java
Object result = new OpenAiActionProcessor().processSingleAction("Book a table for 4 people under the name Maria this Friday");
// Booked a table for 4 under Maria on Friday
```

The rest of this article builds that restaurant assistant step by step, running entirely on your machine.

## What you need to run a local LLM in Java

- Java 17 or newer
- Maven
- [Ollama](https://ollama.com/download), installed and running

Pull the model we will use. It is a 1.9 GB download:

```bash
ollama pull qwen2.5:3b
```

If you just want to see it work, the finished project is in
[examples/ollama-quickstart](https://github.com/vishalmysore/Tools4AI/tree/main/examples/ollama-quickstart).
Clone the repository, go to that folder and run `mvn -q compile exec:java`.

## Step 1: Create the Maven project

Create a Maven project with this `pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <groupId>quickstart</groupId>
    <artifactId>tools4ai-ollama-quickstart</artifactId>
    <version>1.0</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <!-- Keep real parameter names (customerName, not arg0) so the model knows what to fill in -->
        <maven.compiler.parameters>true</maven.compiler.parameters>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.github.vishalmysore</groupId>
            <artifactId>tools4ai</artifactId>
            <version>1.3.0</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>2.0.12</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.5.0</version>
                <configuration>
                    <mainClass>quickstart.QuickStart</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

The one line that matters most is `maven.compiler.parameters`. Tools4AI shows the model your parameter
names. Without this flag Java compiles them as `arg0`, `arg1`, and the model has no idea that `arg0`
is a customer's name.

## Step 2: Write the agent as a plain Java class

An agent is a plain Java class. Mark the class with `@Agent` and each method the model may call with
`@Action`:

```java
package quickstart;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;

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
```

There is no interface to implement and no schema to write. Tools4AI finds the class, creates it, and
reads the parameter types (`String`, `int`) from the method signature.

In a real application these methods would write to a database or call a booking service. The model
never runs your code itself: it only chooses the method and the values, and Tools4AI makes the call.

## Step 3: Connect Java to Ollama

Ollama serves an OpenAI-compatible API, so Tools4AI's OpenAI support works with it unchanged. Create
`src/main/resources/tools4ai.properties`:

```properties
openAiKey=ollama
openAiBaseURL=http://localhost:11434/v1
openAiModelName=qwen2.5:3b
maxTimeOutForOpenAI=3
action.packages.to.scan=quickstart
```

- `openAiKey` can be any non-empty value. Ollama ignores it, but Tools4AI only switches on its OpenAI
  support when a key is present.
- `openAiBaseURL` sends every request to your local Ollama.
- `maxTimeOutForOpenAI` is in minutes. A laptop CPU is slower than a cloud API, so give it room.
- `action.packages.to.scan` tells Tools4AI where your agents live, so it doesn't scan your whole classpath.

Next to it, create `src/main/resources/prompt.properties`:

```properties
openai.post_action=} - reply with the exact name of the
openai.num_action=action that fits best, copied from the list above, and nothing else
```

This changes how Tools4AI words its "which action?" question. Large models are fine with the default
wording, but a 3B model sometimes answers it with a number instead of a method name once an agent has
three or more actions. Spelling out "the exact name" fixes that.

## Step 4: Call the agent from main

```java
package quickstart;

import com.t4a.processor.OpenAiActionProcessor;

public class QuickStart {

    public static void main(String[] args) throws Exception {
        String prompt = args.length > 0
                ? String.join(" ", args)
                : "Book a table for 4 people under the name Maria this Friday";

        Object result = new OpenAiActionProcessor().processSingleAction(prompt);

        System.out.println("Prompt : " + prompt);
        System.out.println("Result : " + result);
    }
}
```

`processSingleAction` is the whole integration. It returns whatever your method returned.

## Run your Java AI agent

```bash
mvn -q compile exec:java
```

```text
Prompt : Book a table for 4 people under the name Maria this Friday
Result : Booked a table for 4 under Maria on Friday
```

Ask something else:

```bash
mvn -q compile exec:java -Dexec.args="Is pizza on the menu today?"
```

```text
Prompt : Is pizza on the menu today?
Result : pizza is on the menu today
```

On a laptop CPU each prompt takes about 10 to 20 seconds with `qwen2.5:3b`, and about 30 seconds in
total once you add Maven's startup. The very first run takes longer while Maven downloads its
dependencies.

You may see a few log lines saying `shell_actions.yaml` or `http_actions.json` were not found. Those are
optional files for other kinds of actions (shell scripts and REST calls) and are safe to ignore. The
finished example in the repository hides them.

## How function calling works under the hood

For each prompt, Tools4AI has a short conversation with the model:

1. **Pick a group.** It lists your agents' groups and descriptions and asks which one fits. Here there is
   only `restaurant`, but with many agents this step keeps the next question short.
2. **Pick an action.** It lists the actions in that group (`bookTable`, `isOnMenu`) and asks for one name.
3. **Fill in the parameters.** It shows the chosen method's parameter names and types and asks the model
   for their values: `customerName=Maria`, `numberOfPeople=4`, `day=Friday`.
4. **Call the method.** It converts the values to the right Java types and invokes `bookTable` through
   reflection.

Because the model only ever answers small, focused questions, a 3B model running locally is enough
for this kind of work.

## Add a new tool with one method

Add a method to `RestaurantAgent`:

```java
@Action(description = "Cancel a booking for a customer")
public String cancelBooking(String customerName) {
    return "Cancelled the booking for " + customerName;
}
```

That is the only change. Run it:

```bash
mvn -q compile exec:java -Dexec.args="Please cancel the booking for Maria"
```

```text
Prompt : Please cancel the booking for Maria
Result : Cancelled the booking for Maria
```

A few things make small models much more reliable:

- **Name parameters for what they hold.** `customerName` tells the model what to extract; `name1` does not.
- **Write the description the way a user would ask.** "Cancel a booking for a customer" matches
  "please cancel the booking for Maria" far better than "Deletes reservation entity".
- **Say values plainly in prompts.** "Cancel the booking for Maria" fills in the name reliably; very
  short prompts such as "Cancel the table for Raj" pick the right action but sometimes leave the
  name empty.
- **Avoid apostrophes in `-Dexec.args`.** The exec plugin treats `Maria's` as an opening quote. This
  only affects passing prompts on the Maven command line, not prompts your application passes in code.

## Which Ollama model is best for Java function calling?

To try another model, `ollama pull` it and change one line in `tools4ai.properties`:

```properties
openAiModelName=phi4
```

Tools4AI reads the properties file first and only falls back to a `-DopenAiModelName=...` system
property when the line is empty. The finished example leaves it empty and defaults to `qwen2.5:3b` in
code, so there you can switch with `mvn -q compile exec:java -DopenAiModelName=phi4`.

| Model | Download | How it did |
|---|---|---|
| `qwen2.5:3b` | 1.9 GB | Picked the right action and filled every parameter for clear prompts. A good default. |
| `phi4` | 9.1 GB | Very reliable, but about five times slower on a CPU. |
| `qwen2.5:1.5b` | 1 GB | Too small: often picks the right action but leaves parameters empty. |

If you have a GPU, Ollama uses it automatically and every model gets much faster.

## Switching to OpenAI, Claude or Gemini later

Nothing in `RestaurantAgent` or `QuickStart` is tied to Ollama. To use OpenAI itself, put a real key in
`openAiKey`, remove `openAiBaseURL`, and set `openAiModelName` to an OpenAI model. Tools4AI also has
processors for Anthropic Claude (`AnthropicActionProcessor`) and Google Gemini
(`GeminiV2ActionProcessor`), configured the same way in `tools4ai.properties`. Your agents stay exactly
as they are.

## Tools4AI vs Spring AI

If you work in Java you have probably heard of [Spring AI](https://spring.io/projects/spring-ai), the
Spring team's AI framework. Both let a model call your Java methods, but they do it in different ways.

**Spring AI uses the model's native tool calling.** It turns your `@Tool` methods into JSON schemas,
sends them with the request, and the model replies with a structured tool call. Spring runs your method
and usually hands the result back to the model, which writes the final answer. That is fast (one or two
model calls), but the model must support tool calling. Many Ollama models do, such as `qwen2.5` and
`llama3.1`, but not all of them.

**Tools4AI asks the model in plain text, one small question at a time**: which group, which action,
which values. It then converts the values to Java types and calls your method itself. Because it never
relies on a special tool-calling API, it works with any chat model, including small or older local models
that have no tool support. The trade-off is speed: each request takes at least three model calls.

| | Spring AI | Tools4AI |
|---|---|---|
| How the method is chosen | The model's native tool-calling API | Plain-text questions, works with any chat model |
| Model calls per request | 1 to 2 | 3 or more |
| What you get back | Usually the model's text answer, written using your tool's result | Your method's return value, unchanged |
| Framework | Built for Spring Boot | Plain Java, with or without Spring |
| Safety built in | You add your own checks | Risk levels on actions (HIGH-risk actions are refused automatically) and human approval |
| Actions without Java code | No | Shell scripts, REST calls and Swagger APIs declared in config files |
| Beyond tool calling | Much broader: RAG and vector stores, chat memory, advisors, structured output, embeddings, image and audio models, MCP, observability | Focused on actions and agents |
| Community | Large, backed by the Spring team | Smaller, open-source project |

**Which one should you use?**

- Pick **Spring AI** if you are already on Spring Boot, your model supports tool calling, and you also
  need RAG, chat memory or observability out of the box.
- Pick **Tools4AI** if you want plain Java without a framework, you are running small local models whose
  tool calling is missing or unreliable, or you want risk gating and human approval for actions built in.

They are not mutually exclusive: Tools4AI is an ordinary Maven dependency, so you can also use it
inside a Spring application.

## Where to go next

Once the basics work, Tools4AI has more to offer for real applications:

- **Risk levels.** Mark a dangerous method `@Action(riskLevel = ActionRisk.HIGH, ...)` and Tools4AI
  refuses to run it automatically when the model chooses it.
- **Human approval.** Pass a `HumanInLoop` implementation to `processSingleAction` to approve or reject
  each call before it runs.
- **Plain answers.** `processor.query("...")` sends a prompt straight to the model when you just want
  text back.

The complete code from this article is in
[examples/ollama-quickstart](https://github.com/vishalmysore/Tools4AI/tree/main/examples/ollama-quickstart).

## FAQ

**Can I use Ollama from Java?**
Yes. Ollama exposes an OpenAI-compatible API at `http://localhost:11434/v1`, so any Java library that
talks to OpenAI can talk to Ollama by changing the base URL. That is all the setup in Step 3 does.

**Do I need an OpenAI API key or a GPU?**
No. The model runs locally through Ollama, so there is no key and no bill. Everything in this article
was tested on a laptop CPU, at about 10 to 20 seconds per request. A GPU makes it much faster.

**Is my data sent anywhere?**
No. The prompt, the model and your Java code all stay on your machine, which matters when the
requests contain customer or company data.

**What is the difference between this and calling the LLM directly?**
Calling the model directly gives you text back. Function calling gives you an *action*: the model
chooses one of your methods and its arguments, and your own code does the work. That is what turns a
chatbot into an agent that can book, cancel, look up or update things in your systems.

**How is this different from Spring AI?**
Spring AI relies on the model's native tool-calling API and is built for Spring Boot. Tools4AI asks the
model in plain text, so it works with any chat model (including small local ones without tool support)
and runs in plain Java, at the cost of a few more model calls per request. See
[Tools4AI vs Spring AI](#tools4ai-vs-spring-ai) above for the full comparison.

**Is it free?**
Yes. Ollama and the models used here are free, and Tools4AI is open source under the MIT license.

**Where is the code?**
The complete example is in
[examples/ollama-quickstart](https://github.com/vishalmysore/Tools4AI/tree/main/examples/ollama-quickstart),
and the library is at [github.com/vishalmysore/Tools4AI](https://github.com/vishalmysore/Tools4AI).
