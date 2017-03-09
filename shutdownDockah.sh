#!/bin/sh
docker ps -all | awk '{if ($2 == "servicecatalog/oscm") print $1;}' | xargs docker stop
