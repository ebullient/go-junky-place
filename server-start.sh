#!/bin/bash

java \
  -Dquarkus.http.host=0.0.0.0 \
  -Dquarkus.http.port=${PORT} \
  -Djava.util.logging.manager=org.jboss.logmanager.LogManager \
  -cp  target/lib \
  -jar target/junky-place-1.0-SNAPSHOT-runner.jar
