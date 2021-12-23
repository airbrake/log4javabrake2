# log4j2 appender for javabrake

[![Build & Test](https://github.com/airbrake/log4javabrake2/actions/workflows/gradle.yml/badge.svg)](https://github.com/airbrake/log4javabrake2/actions/workflows/gradle.yml)

## Installation

Gradle:

```gradle
compile 'io.airbrake:log4javabrake2:0.1.7'
```

Maven:

```xml
<dependency>
  <groupId>io.airbrake</groupId>
  <artifactId>log4javabrake2</artifactId>
  <version>0.1.7</version>
</dependency>
```

Ivy:

```xml
<dependency org='io.airbrake' name='log4javabrake2' rev='0.1.7'>
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

## Notifier release instrucitons

### A note on Java version
Make sure you build and release this notifier with open-jdk-11, one way to manage your local java version is using [asdf](https://asdf-vm.com). You can install this tool via homebrew:
```
brew install asdf
```
Then install open-jdk-11 and set it as JAVA home before running any of the `./gradlew` commands:
```
asdf plugin add java
asdf install java openjdk-11
export JAVA_HOME=$HOME/.asdf/installs/java/openjdk-11
```

### Building and Releasing

```shell
./gradlew build
```

Upload to JCentral:

```shell
./gradlew bintrayUpload
```

Upload to Maven Central:

```shell
./gradlew uploadArchives
./gradlew closeAndReleaseRepository
```

Usefull links:
 - http://central.sonatype.org/pages/gradle.html
 - http://central.sonatype.org/pages/releasing-the-deployment.html
 - https://search.maven.org/artifact/io.airbrake/log4javabrake2
