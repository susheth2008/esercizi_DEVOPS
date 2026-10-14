FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*-runner.jar app.jar
ENV PORT=8080
EXPOSE 8080 
USER 1001
CMD ["java", "-jar", "app.jar"]