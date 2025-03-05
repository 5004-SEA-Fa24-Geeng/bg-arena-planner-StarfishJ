# Report

Submitted report to be manually graded. We encourage you to review the report as you read through the provided
code as it is meant to help you understand some of the concepts. 

## Technical Questions

1. What is the difference between == and .equals in java? Provide a code example of each, where they would return different results for an object. Include the code snippet using the hash marks (```) to create a code block.

   ```java
   // "==" checks for reference equality (memory location)
   // equals() checks for value equality
    public class EqualsVsDoubleEquals {
        public static void main(String[] args) {
            String str1 = new String("String");
            String str2 = new String("String");

            System.out.println(str1 == str2);      
            // false (different memory locations)
            System.out.println(str1.equals(str2)); 
            // true (same value)
        }
    }
   ```

2. Logical sorting can be difficult when talking about case. For example, should "apple" come before "Banana" or after? How would you sort a list of strings in a case-insensitive manner? 

   ```java
    // In case-insensitive sorting, "apple" should come after "Banana."
    import java.util.Arrays;  

    public class Main {  
        public static void main(String[] args) {  
            String[] fruits = {"apple", "Banana", "cherry", "date"};  
            Arrays.sort(fruits, String.CASE_INSENSITIVE_ORDER);  
            System.out.println(Arrays.toString(fruits));  
            // "apple", "Banana", "cherry", "date"
        }  
    }
   ```


3. In our version of the solution, we had the following code (snippet)
    ```java
    public static Operations getOperatorFromStr(String str) {
        if (str.contains(">=")) {
            return Operations.GREATER_THAN_EQUALS;
        } else if (str.contains("<=")) {
            return Operations.LESS_THAN_EQUALS;
        } else if (str.contains(">")) {
            return Operations.GREATER_THAN;
        } else if (str.contains("<")) {
            return Operations.LESS_THAN;
        } else if (str.contains("=="))...
    }
    ```
    Why would the order in which we checked matter (if it does matter)? Provide examples either way proving your point. 
    ```java
    // If we checked " > " or " < " first, then " >= " and " <= " would never match correctly
    // Example:
    public static Operations getOperatorFromStr(String str) {
        if (str.contains(">")) {
            return Operations.GREATER_THAN;
        } else if (str.contains(">=")) { // This will never execute
            return Operations.GREATER_THAN_EQUALS;
        }
        return null;
    }
    ```

4. What is the difference between a List and a Set in Java? When would you use one over the other? 
- List: Ordered, allows duplicates (e.g., ArrayList, LinkedList). Use a List when ordering matters or duplicates are required.
- Set: Unordered, no duplicates allowed (e.g., HashSet, TreeSet). Use a Set when you need unique elements and fast lookups.

5. In [GamesLoader.java](src/main/java/student/GamesLoader.java), we use a Map to help figure out the columns. What is a map? Why would we use a Map here? 

-A Map is a key-value pair data structure. It is useful in GamesLoader.java to map column names to their respective indices, allowing fast lookups without iterating over an array.

6. [GameData.java](src/main/java/student/GameData.java) is actually an `enum` with special properties we added to help with column name mappings. What is an `enum` in Java? Why would we use it for this application?

- An enum is a special Java type used to define a fixed set of constants. In GameData.java, it allows mapping column names to specific properties.

7. Rewrite the following as an if else statement inside the empty code block.
    ```java
    switch (ct) {
        case CMD_QUESTION: // same as help
        case CMD_HELP:
            processHelp();
            break;
        case INVALID:
        default:
            CONSOLE.printf("%s%n", ConsoleText.INVALID);
    }
    ``` 

    ```java
    // your code here, don't forget the class name that is dropped in the switch block..
    if (ct == CMD_QUESTION || ct == CMD_HELP) {
        processHelp();
    } else if (ct == INVALID) {
        CONSOLE.printf("%s%n", ConsoleText.INVALID);
    } else {
        CONSOLE.printf("%s%n", ConsoleText.INVALID);
    }
    ```

## Deeper Thinking

ConsoleApp.java uses a .properties file that contains all the strings
that are displayed to the client. This is a common pattern in software development
as it can help localize the application for different languages. You can see this
talked about here on [Java Localization – Formatting Messages](https://www.baeldung.com/java-localization-messages-formatting).

Take time to look through the console.properties file, and change some of the messages to
another language (probably the welcome message is easier). It could even be a made up language and for this - and only this - alright to use a translator. See how the main program changes, but there are still limitations in the current layout. 

Post a copy of the run with the updated languages below this. Use three back ticks (```) to create a code block. 

```text
*******Welcome to the BoardGame Arena Planner 2025! *******

Author: Yuchen Huang
A tool to help people plan which games they 
want to play on Board Game Arena. 

To get started, enter your first command below, or type ? or help for command options.

```

Now, thinking about localization - we have the question of why does it matter? The obvious
one is more about market share, but there may be other reasons.  I encourage
you to take time researching localization and the importance of having programs
flexible enough to be localized to different languages and cultures. Maybe pull up data on the
various spoken languages around the world? What about areas with internet access - do they match? Just some ideas to get you started. Another question you are welcome to talk about - what are the dangers of trying to localize your program and doing it wrong? Can you find any examples of that? Business marketing classes love to point out an example of a car name in Mexico that meant something very different in Spanish than it did in English - however [Snopes has shown that is a false tale](https://www.snopes.com/fact-check/chevrolet-nova-name-spanish/).  As a developer, what are some things you can do to reduce 'hick ups' when expanding your program to other languages?


As a reminder, deeper thinking questions are meant to require some research and to be answered in a paragraph for with references. The goal is to open up some of the discussion topics in CS, so you are better informed going into industry.

- Software localization (L10N) is crucial beyond just market share considerations. While English remains the dominant online language (25.9%), Chinese (19.4%), Spanish (7.9%), and Arabic (5.2%) have significant user bases. With global internet penetration at 93.4% in North America but only 63.8% in Asia, the share of non-English users is expected to rise as internet access expands[1].
- From a business perspective, a well-executed localization strategy can yield significant returns on investment. According to the latest research by CSA Research[2], 65% of consumers in emerging markets prefer to purchase products available in their native language, even if they are more expensive than their English counterparts.
- According to data from McKinsey Global Institute[3], successful localization projects typically allocate 15-20% of their budget to cultural adaptation research. These investments yield significant returns in user satisfaction and market acceptance. 

[1] Usage statistics of content languages for websites, W3Techs (2023) - https://w3techs.com/technologies/overview/content_language
[2] Common Sense Advisory/CSA Research (2020) - "Can't Read, Won't Buy", https://csa-research.com/Insights/ArticleID/545/cant-read-wont-buy-b2c
[3] Managing the forces of fragmentation: How IT can balance local needs and global efficiency in a multipolar world, McKinsey Global Institute, https://www.mckinsey.com/capabilities/mckinsey-digital/our-insights/managing-the-forces-of-fragmentation-how-it-can-balance-local-needs-and-global-efficiency-in-a-multipolar-world