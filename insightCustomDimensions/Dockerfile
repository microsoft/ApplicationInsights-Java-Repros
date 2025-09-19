# ---- Builder Stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and preload dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy sources and build
COPY src ./src
RUN mvn clean package -DskipTests

# Extract layers
RUN java -Djarmode=layertools -jar /app/target/*.jar extract


# the second stage of our build will copy the extracted layers
FROM eclipse-temurin:17-jre-alpine

WORKDIR /application

COPY --from=builder /app/dependencies/ ./dependencies/
COPY --from=builder /app/snapshot-dependencies/ ./snapshot-dependencies/
COPY --from=builder /app/spring-boot-loader/ ./spring-boot-loader/
COPY --from=builder /app/application/ ./application/

# Copy Application Insights agent + config
COPY ./Docker/applicationinsights-agent-3.7.4.jar /application/insight/
COPY ./Docker/applicationinsights.json /application/insight/

# Set AI config
ENV APPLICATIONINSIGHTS_CONFIGURATION_FILE=/application/insight/applicationinsights.json
# Create a group and user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
# Tell docker that all future commands should run as the appuser user
USER appuser

EXPOSE 8080
# Run Spring Boot via JarLauncher with AI agent
ENTRYPOINT ["java", "-javaagent:/application/insight/applicationinsights-agent-3.7.4.jar","org.springframework.boot.loader.launch.JarLauncher"]
