FROM eclipse-termurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests
EXPOSE 8080
CMD ["java, "-jar", "target/*.jar"]