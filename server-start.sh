#!/bin/bash

export JAVA_LIB_DIR=target/lib
export JAVA_APP_DIR=target
export JAVA_APP_JAR=$(cd target; ls junky*-runner.jar)
export JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -Dquarkus.http.port=${PORT}"
./run-java.sh
