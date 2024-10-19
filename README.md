# Cron Expression Parser
This is a CLI to parse and display on a easy-to-understand table the times
that the command will run, and the command itself.

```
cronParser */15 0 1,15 * 1-5 /usr/bin/find
```

Produces:
```
minute        0 15 30 45
hour          0
day of month  1 15
month         1 2 3 4 5 6 7 8 9 10 11 12
day of week   1 2 3 4 5
command       /usr/bin/find
```

# Details Of Implementation
This Parser implementation follows the LEXER => TOKEN => PARSER => CRON JOB EXPRESSION approach.

The code is organised feature based, so there is a package lexer and a package parser, Main connects both of them to 
produce the application result.

# How To Run
## Requirements
This is a Scala project, built using `sbt`, so ideally we will require the `sbt` to be installed.
It can easily be installed using `sdkman` or `homebrew`, more details in https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Mac.html

```shell
#SDKMAN!
sdk install java $(sdk list java | grep -o "\b8\.[0-9]*\.[0-9]*\-tem" | head -1)
sdk install sbt

# Homebrew
brew install sbt
```

## Running The Tests
Once SBT is installed, to run the tests suite, simply use sbt from the root directory of the project.
```shell
sbt test
```
The command will build and run the tests given a output like:
```shell
[info] welcome to sbt 1.10.2 (GraalVM Community Java 21.0.2)
[info] loading settings for project cronparser-build from assembly.sbt ...
[info] loading project definition from /Users/ferranpuig/IdeaProjects/CronParser/project
[info] loading settings for project root from build.sbt ...
[info] set current project to CronParser (in build file:/Users/ferranpuig/IdeaProjects/CronParser/)
[info] compiling 2 Scala sources to /Users/ferranpuig/IdeaProjects/CronParser/target/scala-2.13/classes ...
[info] compiling 4 Scala sources to /Users/ferranpuig/IdeaProjects/CronParser/target/scala-2.13/test-classes ...
[info] TokenizerSpec:
[info] tokenize
[info] - should return the number token when number is provided
[info] - should return the number token when a string is provided
[info] - should return OtherSymbol for unrecognised chars
[info] - should return the expected tokens
[info] HasRangeLimitSpec:
[info] HasRangeLimit
[info] range
[info] - should create range if valid input provided
[info] - should fail to create a range if input is out of the valid range
[info] isInRange
[info] - should return a `true` if the input fits within the HasRangeLimit min and max
[info] - should return a `false` if the input fits out of the HasRangeLimit min and max
[info] atTimes
[info] - should create the Unit with the list provided if all the values are within the limits
[info] - should fail to create the Unit if any of the values is outside the valid limits
[info] exactAt
[info] - should create the unit with one exact value if this value is within the limits
[info] - should fail to create the unit within one exact value if this value is outside the valid limits
[info] every
[info] - should create the unit with a range of values given the `every` step size if it fits in the limits
[info] - should fail to create the unit with the range of values given the `every` step size bigger than the range itself
[info] ParserSpec:
[info] Parser
[info] parserTokens
[info] - should fail if unexpected token at start
[info] - should fail if we do not find all minimum cronParts
[info] - should fail if unexpected token when we are expecting a different one
[info] - should fail if trying to use a value out of range
[info] - should succeed when all tokens in place and valid
[info] parse
[info] - should given an input string should tokenise and parse the string into a CronExpression
[info] - should work for our sample example
[info] CronPartsSpec:
[info] Minute
[info] - should not be possible to create outside the valid minutes in an hour
[info] - should be possible to create if is a valid minute number
[info] - should be possible to create if is a valid list of minutes
[info] - should not be possible to create if is a list containing invalid minutes
[info] - should be possible to create a `every` X minutes if the N is in minutes range
[info] - should not be possible to create a `every` X minutes if the N is NOT in minutes range
[info] Hour
[info] - should not be possible to create outside the valid hours in a day
[info] - should be possible to create if is a valid hour number
[info] - should be possible to create if is a valid list of hours
[info] - should not be possible to create if is a list containing invalid hours
[info] - should be possible to create a `every` X hours if the N is in hours range
[info] - should not be possible to create a `every` X hours if the N is NOT in hours range
[info] DayOfTheMonth
[info] - should not be possible to create outside the valid dayOfTheMonths in a day
[info] - should be possible to create if is a valid dayOfTheMonth number
[info] - should be possible to create if is a valid list of dayOfTheMonths
[info] - should not be possible to create if is a list containing invalid dayOfTheMonths
[info] - should be possible to create a `every` X dayOfTheMonths if the N is in dayOfTheMonths range
[info] - should not be possible to create a `every` X dayOfTheMonths if the N is NOT in dayOfTheMonths range
[info] Month
[info] - should not be possible to create outside the valid months in a day
[info] - should be possible to create if is a valid month number
[info] - should be possible to create if is a valid list of months
[info] - should not be possible to create if is a list containing invalid months
[info] - should be possible to create a `every` X months if the N is in months range
[info] - should not be possible to create a `every` X months if the N is NOT in months range
[info] DayOfTheWeek
[info] - should not be possible to create outside the valid dayOfTheWeeks in a day
[info] - should be possible to create if is a valid dayOfTheWeek number
[info] - should be possible to create if is a valid list of dayOfTheWeeks
[info] - should not be possible to create if is a list containing invalid dayOfTheWeeks
[info] - should be possible to create a `every` X dayOfTheWeeks if the N is in dayOfTheWeeks range
[info] - should not be possible to create a `every` X dayOfTheWeeks if the N is NOT in dayOfTheWeeks range
[info] Run completed in 137 milliseconds.
[info] Total number of tests run: 51
[info] Suites: completed 4, aborted 0
[info] Tests: succeeded 51, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 3 s, completed 19 Oct 2024, 17:12:12
```

## Running the App with SBT
An option to run the application can be using directly `sbt`, for that we will need to scape the string argument.
```shell
sbt "run \"*/15 0 1,15 * 1-5 /usr/bin/find\""
```

## Building the JAR to run from the CLI
For the building of the fatJar, we are using `sbt-assembly`, so run the command and this will build a jar for later 
execution:

```shell
sbt assembly
```
Once this has run, you should see the jar in the following path: 
`${projectDir}/target/scala-2.13/CronParser-assembly-0.1.0-SNAPSHOT.jar`

With that jar we can then manually test the parser:
```shell
java -jar target/scala-2.13/CronParser-assembly-0.1.0-SNAPSHOT.jar "*/15 0 1,15 * 1-5 /usr/bin/find"
```
Which produces:
```shell

minute        0 15 30 45
hour          0
day of month  1 15
month         1 2 3 4 5 6 7 8 9 10 11 12
day of week   1 2 3 4 5
command       /usr/bin/find

```


