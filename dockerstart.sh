#!/bin/sh
#move image name to jenkins job variable
docker login --username=tech-oscm --email=tech-oscm@est.fujitsu.com artifactory.intern.est.fujitsu.com:5003 && docker pull artifactory.intern.est.fujitsu.com:5003/ctmg:latest
docker stop default && docker rm default
docker run --add-host=oscm.org:127.0.0.1 --name=default -h default -d -i -p 8080:8080 -p 8081:8081 -p 8048:8048 -p 8037:8037 artifactory.intern.est.fujitsu.com:5003/ctmg:latest
sleep 180