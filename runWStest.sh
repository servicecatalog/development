#!/bin/sh
docker exec -i default ant -f /opt/development/oscm-build/cruisecontrol.xml -lib /opt/development/libraries/eclipse-ecj/javalib/ _webserviceTests -Dhostname=default
