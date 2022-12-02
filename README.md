# log4j2 appender for javabrake

[![Build & Test](https://github.com/airbrake/log4javabrake2/actions/workflows/gradle.yml/badge.svg)](https://github.com/airbrake/log4javabrake2/actions/workflows/gradle.yml)

## Introduction

log4javabrake2 is a logging Middleware in Java for Airbrake.

## Installation

Gradle:

```gradle
compile 'io.airbrake:log4javabrake2:0.1.9'
```

Maven:

```xml
<dependency>
  <groupId>io.airbrake</groupId>
  <artifactId>log4javabrake2</artifactId>
  <version>0.1.9</version>
</dependency>
```

Ivy:

```xml
<dependency org='io.airbrake' name='log4javabrake2' rev='0.1.9'>
  <artifact name='log4javabrake2' ext='pom'></artifact>
</dependency>
```

## Configuration
If you want to send the error logs to Airbrake, you need to have following lines in log4j.xml. Add this file in resources folder. This file is main file for log4j configuration. and contains information about log levels, log appenders.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" packages="io.airbrake.log4javabrake2">
  <Appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
    </Console>
    <Airbrake name="Airbrake" projectId="12345" projectKey="FIXME" env="production"></Airbrake>
  </Appenders>
  <Loggers>
    <Root>
      <AppenderRef ref="Console"/>
      <AppenderRef ref="Airbrake"/>
    </Root>
  </Loggers>
</Configuration>
```

## Error Logging

```java
Logger logger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("Name");

try {
  do();
} catch (IOException e) {
  logger.error(e.getMessage());   
}
```

## Notifier release instrucitons

### A note on Java version
Make sure you build and release this notifier with open-jdk, one way to manage your local java version is using [asdf](https://asdf-vm.com). You can install this tool via homebrew:
```
brew install asdf
```
Then install open-jdk-'mention version here' and set it as JAVA home before running any of the `./gradlew` commands:
```
asdf plugin add java
asdf install java openjdk-'mention version here'
export JAVA_HOME=$HOME/.asdf/installs/java/openjdk-'mention version here'
```

### Building and Releasing

```shell
./gradlew build
```
Upload to Maven Central:

```shell
./gradlew publish
```

To release the deployment to maven central repository:
 - http://central.sonatype.org/pages/releasing-the-deployment.html

Usefull links:
 - https://search.maven.org/artifact/io.airbrake/log4javabrake2
