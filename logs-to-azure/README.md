# Introduction 
Trying to send the application logs to azure  application insight using < connection string> 
Issue : Azure opentelemetry sdk is not able to forward/send the SLF4J or Log4j2 or logging  frameworks logs to azure application insights workspace


# Getting Started
TODO: Guide users through getting your code up and running on their own system. In this section you can talk about:
1.	Installation process 
2.	Software dependencies : Spring boot 3.5.6 , java 21

# Build and Test
After downloaded the repository to local

Open the application.properties file under location: /src/main/java/resources

Add/replace the Connection_String parameter value to your Azure application insight workspace's connection String value

Build : run mvn install command

Run  : Run the Application springboot application

Test: Call the API http://localhost:8090/say-hello 

Then you can able to see that different log statements are getting generated to CONSOLE
