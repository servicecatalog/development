The sample implementation of the external subscription parameter configurator tool. 

To use the external configurator tool in CT-MG, perform the following:

1. Deploy the oscm-parameter-configurator.war on a glassfish domain.
2. Adapt some technical service definition to your environment.
3. Login to the administration portal as a technology provider.
4. Import technical service definition.
5. Login to the administration portal as a supplier.
6. Define a marketable service and in the "External configurator url" field specify: 

http://<hostname>:<port>/oscm-parameter-configurator/index.html

Please note that the parameters must be configurable by the user, in order to appear in the external configurator tool.

7. Login as customer to the marketplace portal and subscribe to the defined marketable service by clicking on 
the "Get it now" button.
8. Click on the "Configure" button. 
A modal window appears with the sample parameter configurator if available.
In case the external configurator tool is not available at the specified URL, an appropriate warning message appears and the 
default parameter table is used instead.
9. Modify the parameters as you wish and click on "Save" in the modal window.The modal window closes.
10. Proceed with the rest of the subscription steps.
