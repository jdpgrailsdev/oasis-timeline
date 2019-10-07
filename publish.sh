#!/bin/bash

read -p 'Github Username: ' username
read -sp 'Github Password: ' password

./gradlew clean build moveArtifacts gitPublishPush -Dorg.ajoberstar.grgit.auth.username=$username -Dorg.ajoberstar.grgit.auth.password=$password