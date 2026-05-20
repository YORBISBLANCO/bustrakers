FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests
EXPOSE 8080
CMD ["java", "-jar", "target/*.jar"]
