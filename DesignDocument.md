# Board Game Arena Planner Design Document


This document is meant to provide a tool for you to demonstrate the design process. You need to work on this before you code, and after have a finished product. That way you can compare the changes, and changes in design are normal as you work through a project. It is contrary to popular belief, but we are not perfect our first attempt. We need to iterate on our designs to make them better. This document is a tool to help you do that.


## (INITIAL DESIGN): Class Diagram 

Place your class diagrams below. Make sure you check the file in the browser on github.com to make sure it is rendering correctly. If it is not, you will need to fix it. As a reminder, here is a link to tools that can help you create a class diagram: [Class Resources: Class Design Tools](https://github.com/CS5004-khoury-lionelle/Resources?tab=readme-ov-file#uml-design-tools)

### Provided Code

Provide a class diagram for the provided code as you read through it.  For the classes you are adding, you will create them as a separate diagram, so for now, you can just point towards the interfaces for the provided code diagram.


```mermaid
classDiagram
direction BT
class BGArenaPlanner {
  - BGArenaPlanner() 
  - String DEFAULT_COLLECTION
  + main(String[]) void
}
class BoardGame {
  + BoardGame(String, int, int, int, int, int, double, int, double, int) 
  - String name
  - int maxPlayTime
  - int minPlayTime
  - int minPlayers
  - int maxPlayers
  - int rank
  - int id
  - double averageRating
  - double difficulty
  - int yearPublished
  + getDifficulty() double
  + equals(Object) boolean
  + getRank() int
  + hashCode() int
  + getId() int
  + getMaxPlayers() int
  + getName() String
  + getMinPlayTime() int
  + getYearPublished() int
  + getRating() double
  + getMaxPlayTime() int
  + main(String[]) void
  + getMinPlayers() int
  + toString() String
  + toStringWithInfo(GameData) String
}
class ConsoleApp {
  + ConsoleApp(IGameList, IPlanner) 
  - IGameList gameList
  - String DEFAULT_FILENAME
  - Random RND
  - Scanner current
  - Scanner IN
  - IPlanner planner
  - getInput(String, Object[]) String
  - processFilter() void
  - printOutput(String, Object[]) void
  - printCurrentList() void
  - printFilterStream(Stream~BoardGame~, GameData) void
  - randomNumber() void
  - processListCommands() void
  - remainder() String
  + start() void
  - nextCommand() ConsoleText
  - processHelp() void
}
class ConsoleText {
<<enumeration>>
  + ConsoleText() 
  +  CMD_SORT_OPTION
  +  HELP
  +  PROMPT
  +  FILTERED_CLEAR
  +  CMD_QUESTION
  +  CMD_LIST
  +  CMD_CLEAR
  +  GOODBYE
  +  CMD_SORT_OPTION_DIRECTION_ASC
  +  WELCOME
  +  CMD_HELP
  +  CMD_FILTER
  +  CMD_OPTION_ALL
  - Properties CTEXT
  +  EASTER_EGG
  +  CMD_ADD
  +  CMD_SAVE
  +  FILTER_HELP
  +  LIST_HELP
  +  NO_FILTER
  +  CMD_EXIT
  +  CMD_SHOW
  +  INVALID
  +  CMD_EASTER_EGG
  +  CMD_REMOVE
  +  NO_GAMES_LIST
  +  INVALID_LIST
  +  CMD_SORT_OPTION_DIRECTION_DESC
  + fromString(String) ConsoleText
  + toString() String
  + values() ConsoleText[]
  + valueOf(String) ConsoleText
}
class GameData {
<<enumeration>>
  - GameData(String) 
  +  NAME
  +  YEAR
  +  MAX_TIME
  - String columnName
  +  MIN_TIME
  +  DIFFICULTY
  +  RANK
  +  ID
  +  MAX_PLAYERS
  +  RATING
  +  MIN_PLAYERS
  + getColumnName() String
  + fromString(String) GameData
  + values() GameData[]
  + valueOf(String) GameData
  + fromColumnName(String) GameData
}
class GameList {
  + GameList() 
  - Set~BoardGame~ gameList
  + addToList(String, Stream~BoardGame~) void
  + getGameNames() List~String~
  + count() int
  + clear() void
  + saveGame(String) void
  + removeFromList(String) void
}
class GamesLoader {
  - GamesLoader() 
  - String DELIMITER
  + loadGamesFile(String) Set~BoardGame~
  - toBoardGame(String, Map~GameData, Integer~) BoardGame?
  - processHeader(String) Map~GameData, Integer~
}
class IGameList {
<<Interface>>
  + String ADD_ALL
  + saveGame(String) void
  + getGameNames() List~String~
  + clear() void
  + addToList(String, Stream~BoardGame~) void
  + count() int
  + removeFromList(String) void
}
class IPlanner {
<<Interface>>
  + reset() void
  + filter(String) Stream~BoardGame~
  + filter(String, GameData) Stream~BoardGame~
  + filter(String, GameData, boolean) Stream~BoardGame~
}
class Operations {
<<enumeration>>
  - Operations(String) 
  +  LESS_THAN_EQUALS
  +  GREATER_THAN
  - String operator
  +  LESS_THAN
  +  GREATER_THAN_EQUALS
  +  EQUALS
  +  CONTAINS
  +  NOT_EQUALS
  + fromOperator(String) Operations
  + valueOf(String) Operations
  + getOperator() String
  + getOperatorFromStr(String) Operations?
  + values() Operations[]
}
class Planner {
  + Planner(Set~BoardGame~) 
  + filter(String, GameData, boolean) Stream~BoardGame~
  + reset() void
  + filter(String, GameData) Stream~BoardGame~
  + filter(String) Stream~BoardGame~
}

ConsoleApp  -->  ConsoleText 
GameList  ..>  IGameList 
Planner  ..>  IPlanner 
```

### Your Plans/Design

Create a class diagram for the classes you plan to create. This is your initial design, and it is okay if it changes. Your starting points are the interfaces. 

```mermaid
classDiagram
direction BT
class Filter {
  - Filter(GameData, Operations, String) 
  - Operations operation
  - GameData column
  - String value
  + parseCondition(String) Filter
  + apply(BoardGame) boolean
  + getOperation() Operations
  - parseFilterValue() Comparable?
  + getColumn() GameData
  - getGameValue(BoardGame) Comparable?
  + getValue() String
}

class IPlanner {
<<Interface>>
  + filter(String) Stream~BoardGame~
  + filter(String, GameData, boolean) Stream~BoardGame~
  + filter(String, GameData) Stream~BoardGame~
  + reset() void
}

class Planner {
  + Planner(Set~BoardGame~) 
  - Set~BoardGame~ allGames
  - List~BoardGame~ filteredGames
  + filter(String, GameData) Stream~BoardGame~
  - applyFilter(List~BoardGame~, String) List~BoardGame~
  - createComparator(GameData) Comparator~BoardGame~
  + filter(String) Stream~BoardGame~
  + reset() void
  + filter(String, GameData, boolean) Stream~BoardGame~
}

class SortCriterion {
  + SortCriterion() 
  + sortGames(List~BoardGame~, GameData, boolean) void
}

Planner  ..>  IPlanner
Planner  -->  Filter  
Planner  -->  SortCriterion
```


## (INITIAL DESIGN): Tests to Write - Brainstorm

Write a test (in english) that you can picture for the class diagram you have created. This is the brainstorming stage in the TDD process. 

> [!TIP]
> As a reminder, this is the TDD process we are following:
> 1. Figure out a number of tests by brainstorming (this step)
> 2. Write **one** test
> 3. Write **just enough** code to make that test pass
> 4. Refactor/update  as you go along
> 5. Repeat steps 2-4 until you have all the tests passing/fully built program

You should feel free to number your brainstorm. 

    
    @Test
    public void testFilterName() {
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("name == Go").toList();
        assertEquals(1, filtered.size());
        assertEquals("Go", filtered.get(0).getName());
    }




## (FINAL DESIGN): Class Diagram

Go through your completed code, and update your class diagram to reflect the final design. Make sure you check the file in the browser on github.com to make sure it is rendering correctly. It is normal that the two diagrams don't match! Rarely (though possible) is your initial design perfect. 

For the final design, you just need to do a single diagram that includes both the original classes and the classes you added. 

> [!WARNING]
> If you resubmit your assignment for manual grading, this is a section that often needs updating. You should double check with every resubmit to make sure it is up to date.





## (FINAL DESIGN): Reflection/Retrospective

> [!IMPORTANT]
> The value of reflective writing has been highly researched and documented within computer science, from learning to information to showing higher salaries in the workplace. For this next part, we encourage you to take time, and truly focus on your retrospective.

Take time to reflect on how your design has changed. Write in *prose* (i.e. do not bullet point your answers - it matters in how our brain processes the information). Make sure to include what were some major changes, and why you made them. What did you learn from this process? What would you do differently next time? What was the most challenging part of this process? For most students, it will be a paragraph or two. 
