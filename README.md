# log4j2 appender for javabrake

[![Build & Test](https://github.com/airbrake/log4javabrake2/actions/workflows/gradle.yml/badge.svg)](https://github.com/airbrake/log4javabrake2/actions/workflows/gradle.yml)

## Installation

Gradle:

```gradle
compile 'io.airbrake:log4javabrake2:0.1.3'
```

Maven:

```xml
<dependency>
  <groupId>io.airbrake</groupId>
  <artifactId>log4javabrake2</artifactId>
  <version>0.1.3</version>
</dependency>
```

Ivy:

```xml
<dependency org='io.airbrake' name='log4javabrake2' rev='0.1.3'>
  <artifact name='log4javabrake2' ext='pom'></artifact>
</dependency>
```

## Configuration

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
