#!/bin/sh
docker pull servicecatalog/oscm:latest
docker run -d -i -p 8080:8080 servicecatalog/oscm:latest
sleep 150