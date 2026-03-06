#!/bin/bash -ex

export JAVA_HOME=`/usr/libexec/java_home -v 21`
echo `java -version`
./gradlew clean build