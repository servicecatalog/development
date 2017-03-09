#!/bin/sh
docker ps -all | awk '{if ($2 == "servicecatalog/oscmglassfish4") print $1;}'
docker ps -all | awk '{if ($2 == "servicecatalog/oscmglassfish4") print $1;}' | xargs docker stop
