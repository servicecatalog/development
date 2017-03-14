#!/bin/sh
mv enterprise/Development/branding Development/
mv enterprise/Development/birt Development/
mv enterprise/Development/escm-oscm-devruntime Development/
mv enterprise/Development/ess-oscm-app Development/
mv enterprise/Development/ess-oscm-app-converter Development/
mv enterprise/Development/ess-oscm-app-converter-unittests Development/
mv enterprise/Development/ess-oscm-app-ear Development/
mv enterprise/Development/ess-oscm-app-extsvc-1-0 Development/
mv enterprise/Development/ess-oscm-application Development/
mv enterprise/Development/ess-oscm-app-tps5 Development/
mv enterprise/Development/ess-oscm-app-tps5-unittest Development/
mv enterprise/Development/ess-oscm-app-unittests Development/
mv enterprise/Development/ess-oscm-converter Development/
mv enterprise/Development/ess-oscm-converter-unittests Development/
mv enterprise/Development/ess-oscm-ear Development/
mv enterprise/Development/ess-oscm-tps5-install-pack Development/
mv enterprise/Development/ess-oscm-triggerservice Development/
mv enterprise/Development/ess-oscm-webservices-v1_6-samlsp-tests Development/
mv enterprise/Development/ess-oscm-webservices-v1_7-tests Development/
mv enterprise/Development/ess-oscm-webservices-v1_8-tests Development/
mv enterprise/Development/tps5 Development/

# Replace build ID with the Jenkins variable + Ant
set ANT_OPTS=Xms4000m
/opt/apache-ant-1.9.2/bin/ant -file Development/oscm-build/cruisecontrol.xml -Dbuildid.prefix=GL4_with_docker_pull_ESS -lib /opt/jdk1.7.0_45/lib/tools.jar -Dhttps.proxyHost=proxy.intern.est.fujitsu.com -Dhttps.proxyPort=8080 -Dhttp.proxyHost=proxy.intern.est.fujitsu.com -Dhttp.proxyPort=8080 prepareForDocker

cp Development/oscm-build/result/package/oscm-install-pack/oscm-install-pack.zip docker/
cp Development/oscm-build/result/package/oscm-integrationtests-mockproduct/oscm-integrationtests-mockproduct.war docker/