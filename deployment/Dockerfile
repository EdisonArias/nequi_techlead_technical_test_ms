FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY /applications/app-service/build/libs/nequi_techlead_technical_test_ms.jar nequi_techlead_technical_test_ms.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

USER appuser

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar nequi_techlead_technical_test_ms.jar"]
