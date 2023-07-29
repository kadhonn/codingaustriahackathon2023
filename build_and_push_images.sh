#!/bin/sh

set -e

#build and push backend
cd backend
./gradlew bootJar
docker build -t registry.slothyx.com/nicerpricer-backend .
docker push registry.slothyx.com/nicerpricer-backend
cd ..

#build and push frontend
cd frontend
npm install
npm run build
docker build -t registry.slothyx.com/nicerpricer-frontend .
docker push registry.slothyx.com/nicerpricer-frontend
cd ..
