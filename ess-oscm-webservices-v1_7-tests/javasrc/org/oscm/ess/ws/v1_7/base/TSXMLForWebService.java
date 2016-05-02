/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016
 *                                                                              
 *  Author: weiser                                           
 *                                                                              
 *  Creation Date: 25.08.2010                                                      
 *                                                                              
 *  Completion Time: 25.08.2010                                          
 *                                                                              
 *******************************************************************************/

package org.oscm.ess.ws.v1_7.base;

/**
 * Utility class providing methods for technical product xml creation.
 */
public class TSXMLForWebService {

    private static final String TARGET_NAMESPACE = "xmlns:tns=\"fujitsu.adm.um.serviceprovisioning/1.7/TechnicalService.xsd\"";
    private static final String TNS = "tns:";
    private static final String XML_LAST_PART = "</" + TNS
            + "TechnicalService></" + TNS + "TechnicalServices>";
    private static final String XML_FIRST_PART = "<" + TNS
            + "TechnicalServices " + TARGET_NAMESPACE + ">";

    private TSXMLForWebService() {
    }

    public static String createTSXMLWithTags(String technicalProductId)
            throws Exception {

        String baseUrl = WebserviceTestBase
                .getConfigSetting(WebserviceTestBase.EXAMPLE_BASE_URL);

        StringBuilder sb = new StringBuilder();
        sb.append(XML_FIRST_PART);
        sb.append("<tns:TechnicalService ");
        sb.append("accessType=\"DIRECT\" ");
        sb.append("allowingOnBehalfActing=\"false\" ");
        sb.append("baseUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct/ProvisioningService?wsdl\" ");
        sb.append("build=\"\" ");
        sb.append("id=\"" + technicalProductId + "\" ");
        sb.append("loginPath=\"\" ");
        sb.append("onlyOneSubscriptionPerUser=\"false\" ");
        sb.append("provisioningType=\"SYNCHRONOUS\" ");
        sb.append("provisioningUrl=\"\" ");
        sb.append("provisioningUsername=\"admin\" ");
        sb.append("provisioningPassword=\"adminadmin\" ");
        sb.append("provisioningVersion=\"\">");

        sb.append("<AccessInfo locale=\"en\">AccessInfo</AccessInfo>");
        sb.append("<AccessInfo locale=\"de\"/>");

        sb.append("<LocalizedDescription locale=\"de\"/>");
        sb.append("<LocalizedLicense locale=\"de\"/>");
        sb.append("<LocalizedTag locale=\"en\">enterprise_en</LocalizedTag>");
        sb.append("<LocalizedTag locale=\"en\">service_en</LocalizedTag>");
        sb.append("<LocalizedTag locale=\"de\">enterprise_de</LocalizedTag>");
        sb.append("<LocalizedTag locale=\"de\">service_de</LocalizedTag>");
        sb.append(XML_LAST_PART);

        return sb.toString();
    }

    public static String createTSXMLWithAllowingOnBehalfActingConnectable(
            String booleanValue) throws Exception {

        String baseUrl = WebserviceTestBase
                .getConfigSetting(WebserviceTestBase.EXAMPLE_BASE_URL);
        String xml = "<"
                + TNS
                + "TechnicalServices "
                + TARGET_NAMESPACE
                + ">"
                + " <"
                + TNS
                + "TechnicalService id=\"tp1\" build=\"1\" provisioningType=\"SYNCHRONOUS\""
                + " provisioningUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct/ProvisioningService?wsdl\""
                + " provisioningVersion=\"1.0\" accessType=\"DIRECT\""
                + " baseUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct\""
                + " loginPath=\"/login\" provisioningTimeout=\"50000\""
                + " provisioningUsername=\"admin\" provisioningPassword=\"adminadmin\" allowingOnBehalfActing=\""
                + booleanValue
                + "\">"
                + " <AccessInfo locale=\"en\">AccessInfo</AccessInfo>"
                + " <LocalizedDescription locale=\"en\">LocalizedDescription</LocalizedDescription>"
                + " <LocalizedLicense locale=\"en\">LocalizedLicense</LocalizedLicense>"
                + " <Event id=\"TEST\"><LocalizedDescription locale=\"en\">Test event</LocalizedDescription></Event>"
                + XML_LAST_PART;
        return xml;
    }

    public static String createTSXML() throws Exception {
        return createTSXML("tp1");
    }

    public static String createTSXML(String serviceId) throws Exception {
        String baseUrl = WebserviceTestBase
                .getConfigSetting(WebserviceTestBase.EXAMPLE_BASE_URL);
        String xml = "<"
                + TNS
                + "TechnicalServices "
                + TARGET_NAMESPACE
                + ">"
                + " <"
                + TNS
                + "TechnicalService id=\""
                + serviceId
                + "\" build=\"1\" provisioningType=\"SYNCHRONOUS\""
                + " provisioningUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct/ProvisioningService?wsdl\""
                + " provisioningVersion=\"1.0\" accessType=\"DIRECT\""
                + " baseUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct\""
                + " loginPath=\"/login\" provisioningTimeout=\"50000\""
                + " provisioningUsername=\"admin\" provisioningPassword=\"adminadmin\">"
                + " <AccessInfo locale=\"en\">AccessInfo</AccessInfo>"
                + " <LocalizedDescription locale=\"en\">LocalizedDescription</LocalizedDescription>"
                + " <LocalizedLicense locale=\"en\">LocalizedLicense</LocalizedLicense>"
                + " <Event id=\"TEST\"><LocalizedDescription locale=\"en\">Test event</LocalizedDescription></Event>"
                + XML_LAST_PART;
        return xml;
    }

    public static String createTSXMLWithModificationTypeAttribute(
            String serviceId) throws Exception {
        String baseUrl = WebserviceTestBase
                .getConfigSetting(WebserviceTestBase.EXAMPLE_BASE_URL);
        String xml = "<"
                + TNS
                + "TechnicalServices "
                + TARGET_NAMESPACE
                + ">"
                + " <"
                + TNS
                + "TechnicalService id=\""
                + serviceId
                + "\" build=\"1\" provisioningType=\"SYNCHRONOUS\""
                + " provisioningUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct/ProvisioningService?wsdl\""
                + " provisioningVersion=\"1.0\" accessType=\"DIRECT\""
                + " baseUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct\""
                + " loginPath=\"/login\" provisioningTimeout=\"50000\""
                + " provisioningUsername=\"admin\" provisioningPassword=\"adminadmin\">"
                + " <AccessInfo locale=\"en\">AccessInfo</AccessInfo>"
                + " <LocalizedDescription locale=\"en\">LocalizedDescription</LocalizedDescription>"
                + " <LocalizedLicense locale=\"en\">LocalizedLicense</LocalizedLicense>"
                + " <ParameterDefinition id=\"StandardDef\" valueType=\"INTEGER\" mandatory=\"false\""
                + " configurable=\"true\" minValue=\"12\" maxValue=\"500\" default=\"200\""
                + " modificationType=\"STANDARD\">"
                + "<LocalizedDescription locale=\"en\">Parameter Test</LocalizedDescription>"
                + "</ParameterDefinition>"
                + " <ParameterDefinition id=\"OneTimeDef\" valueType=\"INTEGER\" mandatory=\"false\""
                + " configurable=\"true\" minValue=\"12\" maxValue=\"400\" default=\"100\""
                + " modificationType=\"ONE_TIME\">"
                + "<LocalizedDescription locale=\"en\">Parameter Test1</LocalizedDescription>"
                + "</ParameterDefinition>"
                + " <Event id=\"TEST\"><LocalizedDescription locale=\"en\">Test event</LocalizedDescription></Event>"
                + XML_LAST_PART;
        return xml;
    }

    public static String createTSXMLWithModificationTypeAttribute()
            throws Exception {

        String baseUrl = WebserviceTestBase
                .getConfigSetting(WebserviceTestBase.EXAMPLE_BASE_URL);
        String xml = "<"
                + TNS
                + "TechnicalServices "
                + TARGET_NAMESPACE
                + ">"
                + " <"
                + TNS
                + "TechnicalService id=\"technicalProduct2\" build=\"1\" provisioningType=\"SYNCHRONOUS\""
                + " provisioningUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct/ProvisioningService?wsdl\""
                + " provisioningVersion=\"1.0\" accessType=\"DIRECT\""
                + " baseUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct\""
                + " loginPath=\"/login\" provisioningTimeout=\"50000\""
                + " provisioningUsername=\"admin\" provisioningPassword=\"adminadmin\">"
                + " <AccessInfo locale=\"en\">AccessInfo</AccessInfo>"
                + " <LocalizedDescription locale=\"en\">LocalizedDescription</LocalizedDescription>"
                + " <LocalizedLicense locale=\"en\">LocalizedLicense</LocalizedLicense>"
                + " <ParameterDefinition id=\"TEST\" valueType=\"INTEGER\" mandatory=\"false\""
                + " configurable=\"true\" minValue=\"12\" maxValue=\"400\" default=\"100\""
                + " modificationType=\"ONE_TIME\">"
                + "<LocalizedDescription locale=\"en\">Parameter Test1</LocalizedDescription>"
                + "</ParameterDefinition>"
                + " <Event id=\"TEST\"><LocalizedDescription locale=\"en\">Test event</LocalizedDescription></Event>"
                + XML_LAST_PART;
        return xml;
    }

    public static String createTSXMLWithModificationTypeAttribute(
            String serviceId, String modificationType) throws Exception {

        String baseUrl = WebserviceTestBase
                .getConfigSetting(WebserviceTestBase.EXAMPLE_BASE_URL);
        String xml = "<"
                + TNS
                + "TechnicalServices "
                + TARGET_NAMESPACE
                + ">"
                + " <"
                + TNS
                + "TechnicalService id=\""
                + serviceId
                + "\" build=\"1\" provisioningType=\"SYNCHRONOUS\""
                + " provisioningUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct/ProvisioningService?wsdl\""
                + " provisioningVersion=\"1.0\" accessType=\"DIRECT\""
                + " baseUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct\""
                + " loginPath=\"/login\" provisioningTimeout=\"50000\""
                + " provisioningUsername=\"admin\" provisioningPassword=\"adminadmin\">"
                + " <AccessInfo locale=\"en\">AccessInfo</AccessInfo>"
                + " <LocalizedDescription locale=\"en\">LocalizedDescription</LocalizedDescription>"
                + " <LocalizedLicense locale=\"en\">LocalizedLicense</LocalizedLicense>"
                + " <ParameterDefinition id=\"TEST\" valueType=\"INTEGER\" mandatory=\"false\""
                + " configurable=\"true\" minValue=\"12\" maxValue=\"500\" default=\"200\""
                + " modificationType=\""
                + modificationType
                + "\">"
                + "<LocalizedDescription locale=\"en\">Parameter Test</LocalizedDescription>"
                + "</ParameterDefinition>"
                + " <ParameterDefinition id=\"TEST1\" valueType=\"INTEGER\" mandatory=\"false\""
                + " configurable=\"true\" minValue=\"12\" maxValue=\"400\" default=\"100\""
                + " modificationType=\"STANDARD\">"
                + "<LocalizedDescription locale=\"en\">Parameter Test1</LocalizedDescription>"
                + "</ParameterDefinition>"
                + " <Event id=\"TEST\"><LocalizedDescription locale=\"en\">Test event</LocalizedDescription></Event>"
                + XML_LAST_PART;
        return xml;
    }

    public static String createAsynTSXML() throws Exception {

        String baseUrl = WebserviceTestBase
                .getConfigSetting(WebserviceTestBase.EXAMPLE_BASE_URL);
        String xml = "<"
                + TNS
                + "TechnicalServices "
                + TARGET_NAMESPACE
                + ">"
                + " <"
                + TNS
                + "TechnicalService id=\"asynTechnicalProduct\" build=\"1\" provisioningType=\"ASYNCHRONOUS\""
                + " provisioningUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct/ProvisioningService?wsdl\""
                + " provisioningVersion=\"1.0\" accessType=\"DIRECT\""
                + " baseUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct\""
                + " loginPath=\"/login\" provisioningTimeout=\"50000\""
                + " provisioningUsername=\"admin\" provisioningPassword=\"adminadmin\">"
                + " <AccessInfo locale=\"en\">AccessInfo</AccessInfo>"
                + " <LocalizedDescription locale=\"en\">LocalizedDescription</LocalizedDescription>"
                + " <LocalizedLicense locale=\"en\">LocalizedLicense</LocalizedLicense>"
                + " <ParameterDefinition id=\"TEST\" valueType=\"INTEGER\" mandatory=\"false\""
                + " configurable=\"true\" minValue=\"12\" maxValue=\"400\" default=\"100\""
                + " modificationType=\"ONE_TIME\">"
                + "<LocalizedDescription locale=\"en\">Parameter Test1</LocalizedDescription>"
                + "</ParameterDefinition>"
                + " <Event id=\"TEST\"><LocalizedDescription locale=\"en\">Test event</LocalizedDescription></Event>"
                + XML_LAST_PART;
        return xml;
    }

    /**
     * Creates the XML including the operation 'SNAPSHOT' with parameters
     * 'SERVER' and 'COMMENT'
     * 
     * @param id
     *            the technical service ID
     * @return
     * @throws Exception
     */
    public static String createTSXMLWithOpsAndOpParams(String id)
            throws Exception {
        String baseUrl = WebserviceTestBase
                .getConfigSetting(WebserviceTestBase.EXAMPLE_BASE_URL);
        String xml = "<"
                + TNS
                + "TechnicalServices "
                + TARGET_NAMESPACE
                + "><"
                + TNS
                + "TechnicalService id=\""
                + id
                + "\" build=\"1\" provisioningType=\"SYNCHRONOUS\""
                + " provisioningUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct/ProvisioningService?wsdl\""
                + " provisioningVersion=\"1.0\" accessType=\"DIRECT\""
                + " baseUrl=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct\""
                + " loginPath=\"/login\" provisioningTimeout=\"50000\""
                + " provisioningUsername=\"admin\" provisioningPassword=\"adminadmin\">"
                + " <AccessInfo locale=\"en\">AccessInfo</AccessInfo>"
                + " <LocalizedDescription locale=\"en\">LocalizedDescription</LocalizedDescription>"
                + " <LocalizedLicense locale=\"en\">LocalizedLicense</LocalizedLicense>"
                + "<Operation id=\"SNAPSHOT\" actionURL=\""
                + baseUrl
                + "/oscm-integrationtests-mockproduct/OperationService?wsdl\">"
                + "<LocalizedName locale=\"en\">Help</LocalizedName>"
                + "<LocalizedDescription locale=\"en\">Help activation.</LocalizedDescription>"
                + "<OperationParameter id=\"SERVER\" mandatory=\"true\" type=\"REQUEST_SELECT\">"
                + "<LocalizedName locale=\"en\">Server</LocalizedName>"
                + "</OperationParameter>"
                + "<OperationParameter id=\"COMMENT\" mandatory=\"false\" type=\"INPUT_STRING\">"
                + "<LocalizedName locale=\"en\">Comment</LocalizedName>"
                + "</OperationParameter></Operation>" + XML_LAST_PART;
        return xml;
    }

}
