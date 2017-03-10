#!/bin/sh
docker ps -all | awk '{if ($2 == "servicecatalog/oscm:latest") print $1;}' | xargs docker stop
