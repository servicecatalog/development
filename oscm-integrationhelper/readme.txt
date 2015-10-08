The implementation of the Catalog Manager integration helpers.

The integration helpers use the Catalog Manager SessionService Web service. 
Using the IntegrationHelper.war application, you can test Web service calls:
After deployment, configure the IntegrationHelper.war application as a Web service client for 
Catalog Manager and reload the application.
 
The configuration settings must be specified in the webserviceclient.properties file. 
Adapt them to your Catalog Manager installation.

After having configured the IntegrationHelper.war application, you can test a Web service
call to Catalog Manager using the TestWebService.jsp test page:

https://<host>:<port>/Integrationhelper/TestWebService.jsp

When the Web service call is completed, you are notified about its success or failure.
In case of an error, detailed exception information is output in the test page.

Refer to the Catalog Manager Developer's Guide for details on how to integrate the 
integration helpers in your application. 