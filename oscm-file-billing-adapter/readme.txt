The billing adapter implements the billing interface of OSCM. 
It adapts the external billing system model and interfaces to the ones of OSCM.

The file billing adapter works with a file-based mock billing application which is a 
REST web application.

Billing plugin sample is delivered with billing plugin adapter which allows use it integrated in OSCM:
-	oscm-file-billing.war - sample billing application
-	oscm-file-adapter.ear - adapts external billing to be useful in OSCM

Solution has been developed and tested with Glassfish 3.1.2.2 and this one is recommended.

Deployment and configuration sample billing applications.

    1. Set up portal according to Installation.pdf provided within the install-pack.
    2. Deploy oscm-file-billing mock application.
    3. Deploy oscm-file-adapter and configure it. Assuming that oscm-file-billing application is
       deployed on domain with portbase 8600,
       <glassfish_home>/domains/<domain_name>/applications/oscm-file-adapter/oscm-file-adapter_jar/billingApplication.properties
       file should contains following entries:

       priceModelURL = http://<host>:<port>/oscm-file-billing/rest/priceModel
       priceModelFileURL = http://<host>:<port>/oscm-file-billing/rest/priceModelFile
       testConnectionURL = http://<host>:<port>/oscm-file-billing/rest/application/ping

       where:
       <host> - is host where oscm-file-billing has been deployed
       <port> - port of the domain where oscm-file-billing has been deployed (e.g 8680, for domain with portbase 8600)

    4. Save the configuration file and reload the application using asadmin commandline application or administration console.

Configuration of OSCM
Billing adapter must be registered in the OSCM in order to use freshly deployed billing system delivered with it.
    1. Log in as platform operator to OSCM.
    2. In the Operation -> Manage billing systems, add a new billing system with the following values in the fields:
        Billing Adapter ID:                 FILE_BILLING
        Name:                               File Billing Adapter
        org.omg.CORBA.ORBInitialPort:       <IIOP port>
        java.naming.factory.initial:        com.sun.enterprise.naming.SerialInitContextFactory
        java.naming.provider.url:           http://<host>:<port>
        JNDI_NAME:                          java:global/oscm-file-adapter/oscm-file-adapter
        org.omg.CORBA.ORBInitialHost:       <host>

       where:
       <host> - is host where file billing adapter has been deployed
       <port> - port of the domain where oscm-file-adapter has been deployed (e.g 8680, for domain with portbase 8600)
       <IIOP port> - the IIOP port of the glassfish domain where the oscm-file-adapter is deployed
                    (e.g 8637, for domain with portbase 8600. For more info refer to glassfish administration documentation).
    3. Test connection by clicking 'Test connection' button on the Manage billing systems page.
