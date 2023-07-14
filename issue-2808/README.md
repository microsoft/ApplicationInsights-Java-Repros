

```
mvn clean package

export SERVICE_BUS_CONNECTION_STRING=...
export APPLICATIONINSIGHTS_CONNECTION_STRING=...

java -javaagent:target/agent/applicationinsights-agent.jar -jar target/app.jar
```
