# Cron Expression Parser
This is a CLI to parse and display on a easy-to-understand table the times
that the command will run, and the command itself.
Also display the next 5 expected instances of the cron.

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

Friday, 1 November 2024, 00:00:00 Greenwich Mean Time
Friday, 1 November 2024, 00:15:00 Greenwich Mean Time
Friday, 1 November 2024, 00:30:00 Greenwich Mean Time
Friday, 1 November 2024, 00:45:00 Greenwich Mean Time
Friday, 15 November 2024, 00:00:00 Greenwich Mean Time
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
The command will build and run the tests

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

Friday, 1 November 2024, 00:00:00 Greenwich Mean Time
Friday, 1 November 2024, 00:15:00 Greenwich Mean Time
Friday, 1 November 2024, 00:30:00 Greenwich Mean Time
Friday, 1 November 2024, 00:45:00 Greenwich Mean Time
Friday, 15 November 2024, 00:00:00 Greenwich Mean Time

```


