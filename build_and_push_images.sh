#!/bin/sh


#build and push backend
cd backend
./gradlew bootJar
docker build -t registry.slothyx.com/nicerpricer-backend .
docker push registry.slothyx.com/nicerpricer-backend
cd ..
