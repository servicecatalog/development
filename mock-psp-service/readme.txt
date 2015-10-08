A mock servlet to be used as substitute for a psp integration. 
Will not cause any psp related actions at all.

To activate the servlet in your environment (necessary to register payment information objects 
for customers) please do the following:

1. Deploy the file mock-psp-service.war to the environment you use.
2. Edit the configuration settings of the BES installation. Enter the path to the servlet as value for
the parameter PSP_POST_URL. The path should be

http://<machine>:<port>/mock-psp-service/PSPMockRegistrationEntry


Steps for configuration in secured environment:

1. obtain the BES-server certificate. This can be done by following these steps:
	a) execute command
		%JAVA_HOME%\bin\keytool -export -alias s1as -keystore <glassfish-domain>\config\cacerts.jks -file bessrv.crt
		(the glassfish default keystore password is changeit) 
		
2. import the BES-server certificate into a truststore to be used by the client. 
	a) execute command
		%JAVA_HOME%\bin\keytool -import -trustcacerts -alias bessrv -file bessrv.crt -keystore truststore.jks		
3. in the environment you deployed the mock service in, set the following properties
	a) PSP_MOCK_TRUSTSTORE_PATH=<path-to-file-created-in-step-2>\truststore.jks
	b) PSP_MOCK_TRUSTSTORE_PWD=The password for the truststore file.
	
