# How to migrate from legacy sdk to Java Agent 3.x

How to run:
1. Generate the app jar: `./gradlew clean && ./gradlew build`
2. `export APPLICATIONINSIGHTS_CONNECTION_STRING=<YOUR_APPLICATION_INSIGHTS_CONNECTION_STRING>`
3. `export APPLICATIONINSIGHTS_SELF_DIAGNOSTICS_LEVEL=debug`
4. `java -jar ./build/libs/SpringBoot3xSupport-1.0-SNAPSHOT.jar`
5. Verify that Java agent has been attached successfully by looking for this log
```
Application Insights Java Agent 3.4.11 started successfully
```
6. `curl localhost:8080/greetings`
7. Verify in your log that the following entry is present
```Json
{"ver":1,"name":"Event","time":"2023-04-21T19:20:06.193Z","iKey":"<YOUR_INSTRUMENTATION_KEY>","tags":{"ai.internal.sdkVersion":"ra_java:3.4.11","ai.operation.id":"b8b68707f08f2d85bbde4295ae6e1fb8","ai.cloud.roleInstance":"<YOUR_ROLE_NAME>","ai.operation.name":"GET /greetings","ai.operation.parentId":"b4dc9b606751d0a9"},"data":{"baseType":"EventData","baseData":{"ver":2,"name":"URI /greeting is triggered"}}}
```
6. Alternatively, you can go to Application Insights Azure Portal and verify that the custom event has been sent to your Application Insights Resource.