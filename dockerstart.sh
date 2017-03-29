#!/bin/sh
#move image name to jenkins job variable
docker login --username=tech-oscm --email=tech-oscm@est.fujitsu.com artifactory.intern.est.fujitsu.com:5003 && sudo docker pull artifactory.intern.est.fujitsu.com:5003/ctmg:latest
docker run -d -i -p 8080:8080 artifactory.intern.est.fujitsu.com:5003/ctmg:latest
sleep 150