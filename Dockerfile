FROM gradle:6.6.1-jdk14 as builder

WORKDIR /opt/app

COPY . .
# Build and copy dependencies in a separated directory
RUN ./gradlew --no-daemon build copyDependencies

FROM openjdk:16-jdk-alpine

# Use regular user instead of root
RUN addgroup -g 987 appgrp && \
  adduser -G appgrp -u 987 -D app

USER app

WORKDIR /opt/app

COPY --from=builder /opt/app/build/libs/*.jar ./
COPY --from=builder /opt/app/dependencies/ ./

ENTRYPOINT ["java", "-cp", "*", "io.zeebe.EchoWorker"]