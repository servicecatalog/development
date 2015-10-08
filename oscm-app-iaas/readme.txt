The implementation of the APP core proxy and TPS5 controller for integration with the TPS5 system.


A. The configurationsetting table in the "bssapp" database should contain the following settings:

1. Generic Core APP proxy settings:
APP_ADMIN_MAIL_ADDRESS = saas@est.fujitsu.com;
APP_BASE_URL = http://127.0.0.1:8080/oscm-app;
APP_MAIL_RESOURCE = mail/APPMail;
APP_TIMER_INTERVAL = 15000;
BSS_WEBSERVICE_URL = http://estmengeress:8080/{SERVICE}/v1.3/BASIC?wsdl;

2. TPS5 controller specific settings:
IAAS_API_LOCALE = en
IAAS_API_URI = https://api.globalcloud.de.fujitsu.com/ovissapi/endpoint


For Windows:

B. On the java VM where the APP proxy Core and TPS5 controller is running (e.g. glassfish), you need to specify the following settings:
-Doscm.oviss.keyStore=C:/UserCert.p12 <- the path to the certificate file provided by TPS5 while signing
-Doscm.oviss.keyStoreType=pkcs12
-Doscm.oviss.keyStorePassword=xxx <- the TPS5 account password where the certificate was generated for

-Doscm.oviss.trustStore=C:/OViSSSDK_20120218_en/OViSS_JAVASDK/bin/security/cacerts
-Doscm.oviss.trustStoreType=jks
-Doscm.oviss.trustStorePassword=changeit

C. set the https.proxyHost and https.proxyPort on the java VM where the APP proxy Core and TPS5 controller is running (e.g. glassfish)
-Dhttps.proxyHost=192.168.210.82
-Dhttps.proxyPort=8080

For Linux:

B. On the java VM where the APP proxy Core and TPS5 controller is running (e.g. glassfish), you need to specify the following settings:
-Doscm.oviss.keyStore=/opt/fgcp_cert/Fujitsuglobal/UserCert.p12
-Doscm.oviss.keyStoreType=pkcs12
-Doscm.oviss.trustStore=/opt/fgcp_cert/cacerts
-Doscm.oviss.trustStoreType=jks
-Doscm.oviss.trustStorePassword=changeit
-Doscm.oviss.keyStorePassword=xxxxxx

C. set the https.proxyHost and https.proxyPort on the java VM where the APP proxy Core and TPS5 controller is running (e.g. glassfish)
-Dhttps.proxyHost=192.168.210.82
-Dhttps.proxyPort=8080


