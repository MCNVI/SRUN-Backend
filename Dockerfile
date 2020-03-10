# creates build image with only dependencies,
# it relies on caching for pom.xml won't be changed often
FROM gradle:6.0.1-jdk11 AS build-deps
RUN mkdir -p /usr/src/app
COPY build.gradle.kts /usr/src/app/build.gradle.kts
COPY settings.gradle.kts /usr/src/app/settings.gradle.kts
WORKDIR /usr/src/app
# download dependencies
RUN gradle --refresh-dependencies

COPY src /usr/src/app/src
ARG VERSION
ENV VERSION=$VERSION
# RUN mvn versions:set -DnewVersion=$VERSION
RUN gradle build

# creates runtime
FROM adoptopenjdk/openjdk11:alpine-jre
COPY --from=build-deps /usr/src/app/build/libs/*.jar /app.jar
CMD ["java", "-jar", "/app.jar"]