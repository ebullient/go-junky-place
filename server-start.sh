#!/bin/bash
mkdir -p target/deployments/lib

cp run-java.sh         target/deployments/
cp -R target/lib/*     target/deployments/lib/
cp target/*-runner.jar target/deployments/app.jar

cd target/deployments

export JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -Dquarkus.http.port=${PORT}"
./run-java.sh
