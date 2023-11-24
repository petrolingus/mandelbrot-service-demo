#!/bin/bash

ARTIFACT_NAME="petrolingus/process-service:latest"
DOCKER_FILE="docker/Dockerfile"

echo "=> Build java jar via maven ..."

mvn clean install

echo "=> Building docker image ..."

docker build --file=${DOCKER_FILE} --pull --tag ${ARTIFACT_NAME} .
docker image prune -f

echo "==> Pushed image: ${ARTIFACT_NAME}"
docker push ${ARTIFACT_NAME}
