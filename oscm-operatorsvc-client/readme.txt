The client to invoke the operator service methods.

Currently the used classes are GlassFish-specific.

The build process creates a jar file which can be used with the "java -jar" command. In the path
the jar file is used, the following library files are required:

- oscm-common.jar 
- oscm-extsvc.jar 
- oscm-operatorsvc.jar 
- oscm-devruntime.jar
- appserv-rt.jar 
- gf-client.jar (and the referenced files in manifest)

The following two properties files are also required: 

- env.properties

This file contains the configuration settings required to create a connection with the application server
in order to execute the service call. The contained settings must be adapted to the local environment.

- organization-related properties files (e.g. org.properties, update_org.properties)

The name of the file can be arbitrarily chosen. It has to be specified as command line parameter. The 
content of the file provides the data on the organization and initial user to be created.
