#!/bin/bash

if [ "$1" != "" ]; then
  mvn clean
  mvn package
  docker build -t "$1"/assessment .
  docker push "$1"/assessment:latest
else
  echo "Pass your dockerHub name"
fi
