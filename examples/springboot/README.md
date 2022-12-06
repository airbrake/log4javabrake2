# Spring Boot Sample Application for JavaBrake

## About the application

The example application provides three GET endpoints:

1. `/date` - returns server date and time.
2. `/locations` - returns list of available locations.
3. `/weather/{locationName}` - returns the weather details for the locations.

## Steps to run the API

1. Install the dependencies for the application

    ```
    mvn install
    ```

2. You must get both `projectId` & `projectKey`.

    Find your `projectId` and `projectKey` in your Airbrake account and replace them in `log4j2.xml` file. Also mention project `env` (environment).

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <Configuration status="WARN" packages="io.airbrake.log4javabrake2">
        <Appenders>
            <Console name="Console" target="SYSTEM_OUT">
                <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
            </Console>
            <Airbrake name="Airbrake" projectId="123456" projectKey="FIXME" env="test"></Airbrake>
        </Appenders>
        <Loggers>
            <Root>
            <AppenderRef ref="Console"/>
            <AppenderRef ref="Airbrake"/>
            </Root>
        </Loggers>
    </Configuration>
    ```
3. Need to remove/exclude other logger dependencies. For example -

    In pom.xml -

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    ```

    And add following dependency

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
    ```

    In gradle -

    ```
    implementation ('org.springframework.boot:spring-boot-starter-web')
    {
    exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging'
    }
    ```

    And add following dependency

    ```
    implementation 'org.springframework.boot:spring-boot-starter-log4j2:2.7.3'
    ```

4. Run the application

5. To retrieve the responses, append the endpoints to the localhost URL.

    Use the below curl commands to interact with the endpoints. 

    ```bash
    curl "http://localhost:8080/date" 
    curl "http://localhost:8080/locations"
    curl "http://localhost:8080/weather/<austin/pune/santabarbara>"
    ```

    The below curl command will raise `404 Not Found` error.

    ```bash
    curl -I "http://localhost:8080/weather"
    ```

    The below curl command will raise `500 Internal server error` error.

    ```bash
    # Should produce an intentional HTTP 500 error and report the error to Airbrake (since `washington` is in the supported cities list but there is no data for `washington`, an `if` condition is bypassed and the `data` variable is used but not initialized)
    curl -I "http://localhost:8080/weather/washington"
    ```

    Or you can use Postman application.