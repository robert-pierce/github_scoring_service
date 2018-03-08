# First we set up the environment to build the jar:
FROM thirtyspokes/alpine-lein:jre8 AS builder
MAINTAINER Robert Pierce <rpierce3@gmail.com>

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY project.clj /usr/src/app/
RUN lein deps
COPY . /usr/src/app
RUN mv "$(lein ring uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar
CMD ["java", "-jar", "app-standalone.jar"]

# Then we inject the jar into a much smaller base image:
FROM anapsix/alpine-java
COPY --from=builder /usr/src/app/app-standalone.jar app-standalone.jar
EXPOSE 8000
CMD ["java", "-jar", "app-standalone.jar"]
