# log4j2 appender for javabrake

[![Build Status](https://travis-ci.org/airbrake/log4javabrake2.svg?branch=master)](https://travis-ci.org/airbrake/log4javabrake2)

## Installation

Gradle:

```gradle
compile 'io.airbrake:log4javabrake2:0.1.2'
```

Maven:

```xml
<dependency>
  <groupId>io.airbrake</groupId>
  <artifactId>log4javabrake2</artifactId>
  <version>0.1.2</version>
  <type>pom</type>
</dependency>
```

Ivy:

```xml
<dependency org='io.airbrake' name='log4javabrake2' rev='0.1.2'>
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
    <Airbrake name="Airbrake"></Airbrake>
  </Appenders>
  <Loggers>
    <Root>
      <AppenderRef ref="Console"/>
      <AppenderRef ref="Airbrake"/>
    </Root>
  </Loggers>
</Configuration>
```
