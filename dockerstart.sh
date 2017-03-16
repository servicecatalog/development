#!/bin/sh
#move image name to jenkins job variable 
docker pull servicecatalog/oscm:latest
docker run -d -i -p 8080:8080 servicecatalog/oscm:latest
sleep 150