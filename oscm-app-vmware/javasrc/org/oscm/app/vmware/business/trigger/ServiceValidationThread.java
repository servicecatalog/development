/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) FUJITSU LIMITED - ALL RIGHTS RESERVED.                  
 *       
 *  Creation Date: 2014-05-21                                                       
 *                                                                              
 *******************************************************************************/
package org.oscm.app.vmware.business.trigger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.oscm.app.vmware.business.VM;
import org.oscm.app.vmware.business.VMPropertyHandler;
import org.oscm.app.vmware.i18n.Messages;
import org.oscm.app.vmware.persistence.APPDataAccessService;
import org.oscm.app.vmware.remote.bes.BesClient;
import org.oscm.app.vmware.remote.vmware.VMwareClient;
import org.oscm.app.vmware.remote.vmware.VMwareClientFactory;
import org.oscm.intf.AccountService;
import org.oscm.intf.ServiceProvisioningService;
import org.oscm.intf.TriggerService;
import org.oscm.vo.VOLocalizedText;
import org.oscm.vo.VOOrganization;
import org.oscm.vo.VOParameter;
import org.oscm.vo.VOParameterDefinition;
import org.oscm.vo.VOService;
import org.oscm.vo.VOServiceDetails;
import org.oscm.vo.VOTechnicalService;
import org.oscm.vo.VOTriggerProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim25.ManagedObjectReference;

/**
 * Implements a thread which automatically approves a started trigger.
 */
public class ServiceValidationThread implements Runnable {

    private final static Logger log = LoggerFactory
            .getLogger(ServiceValidationThread.class);

    private final int MAX_ATTEMPTS = 5;
    private final long WAIT_TIME_MS = 5000;

    private VOTriggerProcess process;
    private VOService product;
    private VMwareClientFactory vmwFactory;

    public ServiceValidationThread(VOTriggerProcess process,
            VOService product) {
        this.process = process;
        this.product = product;
    }

    @Override
    public final void run() {

        boolean validationFailed = false;
        String validationMessage = null;

        try {
            String orgId = process.getUser().getOrganizationId();
            VOOrganization org = getOrganization(orgId);
            vmwFactory = new VMwareClientFactory(org.getLocale());

            VOServiceDetails serviceDetails = getServiceDetails(
                    product.getSellerId(), product);
            List<VOParameter> serviceParams = getAllParams(serviceDetails);
            String controllerId = getParamValue("APP_CONTROLLER_ID",
                    serviceParams);

            if (controllerId != null && controllerId.equals("ess.vmware")) {
                validateInstancenamePrefix(org.getLocale(), serviceParams);
                validateTemplate(org.getLocale(), serviceParams);
                validateExternalUISettings(org.getLocale(),
                        serviceDetails.getConfiguratorUrl());
                validateScriptUrl(org.getLocale(), serviceParams);
                validateTargetFolder(org.getLocale(), serviceParams);
                validateVLAN(org.getLocale(), serviceParams);
            } else {
                log.info(
                        "Service parameter validation is only implemented for the VMware controller. controllerId: "
                                + controllerId);
            }
        } catch (Exception t) {
            log.error("Failed to validate service", t);
            validationFailed = true;
            validationMessage = t.getMessage();
        }

        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            try {
                TriggerService trigSvc = (new BesClient())
                        .getWebServiceAsOrganizationAdmin(TriggerService.class,
                                process.getUser().getOrganizationId());
                if (validationFailed) {
                    log.debug("reject action with process key "
                            + process.getKey());
                    VOLocalizedText locReason = new VOLocalizedText("en",
                            validationMessage);
                    List<VOLocalizedText> reasonList = new ArrayList<VOLocalizedText>();
                    reasonList.add(locReason);
                    trigSvc.rejectAction(process.getKey(), reasonList);
                } else {
                    log.debug("approve action with process key "
                            + process.getKey());
                    trigSvc.approveAction(process.getKey());
                }
                break;
            } catch (Exception t) {
                log.debug("notify CTMG process failed. process key: "
                        + process.getKey() + " message: " + t.getMessage());
                try {
                    Thread.sleep(WAIT_TIME_MS);
                } catch (Throwable t2) {
                    log.error("ServiceValidationThread.run()", t2);
                    break;
                }
            }
        }

    }

    private List<VOParameter> getAllParams(VOServiceDetails serviceDetails) {
        log.debug("");
        List<VOParameter> serviceParams = serviceDetails.getParameters();
        List<VOParameterDefinition> techServiceParams = serviceDetails
                .getTechnicalService().getParameterDefinitions();
        for (VOParameterDefinition tp : techServiceParams) {
            boolean found = false;
            for (VOParameter p : serviceParams) {
                if (p.getParameterDefinition().getParameterId()
                        .equals(tp.getParameterId())) {
                    found = true;
                }
            }

            if (!found && tp.getDefaultValue() != null) {
                log.debug(
                        "add missing service parameter from technical service definition. "
                                + tp.getParameterId() + ": "
                                + tp.getDefaultValue());
                VOParameter param = new VOParameter();
                param.setParameterDefinition(tp);
                param.setValue(tp.getDefaultValue());
                param.setConfigurable(tp.isConfigurable());
                serviceParams.add(param);
            }

        }

        List<HierarchicalConfiguration> params = new ArrayList<HierarchicalConfiguration>();
        try {
            APPDataAccessService das = new APPDataAccessService();
            ServiceProvisioningService sps = (new BesClient()).getWebService(
                    ServiceProvisioningService.class,
                    das.loadTechnologyProviderCredentials());

            List<VOTechnicalService> technicalServices = new ArrayList<VOTechnicalService>();
            technicalServices.add(serviceDetails.getTechnicalService());
            byte[] tsvc = sps.exportTechnicalServices(technicalServices);
            InputStream in = new ByteArrayInputStream(tsvc);
            XMLConfiguration xml = new XMLConfiguration();
            xml.setSchemaValidation(false);
            xml.setExpressionEngine(new XPathExpressionEngine());
            xml.load(in);
            params = xml.configurationsAt(
                    "//ParameterDefinition[@configurable=\"false\"]");
            log.debug("adding " + params.size()
                    + " service parameters from technical service XML");
        } catch (Exception e) {
            log.error("Failed to load technical service XML", e);
            return serviceParams;
        }

        for (HierarchicalConfiguration param : params) {
            VOParameterDefinition pd = new VOParameterDefinition();
            pd.setDefaultValue(param.getString("@default"));
            pd.setParameterId(param.getString("@id"));
            boolean mandatory = param.getString("@mandatory") == null ? false
                    : param.getString("@mandatory").equals("true");
            pd.setMandatory(mandatory);
            boolean configurable = param.getString("@configurable") == null
                    ? false : param.getString("@configurable").equals("true");
            pd.setConfigurable(configurable);
            log.debug(
                    "add missing service parameter from technical service XML. "
                            + pd.getParameterId() + ": "
                            + pd.getDefaultValue());

            VOParameter sp = new VOParameter();
            sp.setParameterDefinition(pd);
            sp.setValue(pd.getDefaultValue());
            sp.setConfigurable(pd.isConfigurable());
            serviceParams.add(sp);
        }

        return serviceParams;
    }

    private String getParamValue(String key, List<VOParameter> params) {
        String value = null;
        for (VOParameter par : params) {
            String paramId = par.getParameterDefinition().getParameterId();
            if (key.equals(paramId)) {
                value = par.getValue();
                break;
            }
        }

        return value;
    }

    private void validateExternalUISettings(String locale,
            String configuratorUrl) throws Exception {
        boolean valid = false;
        if (configuratorUrl != null && configuratorUrl.length() > 0) {

            log.debug("configuratorUrl: " + configuratorUrl);
            try {
                URL configUrl = new URL(configuratorUrl);
                HttpURLConnection urlConn = (HttpURLConnection) configUrl
                        .openConnection();
                urlConn.connect();
                valid = (HttpURLConnection.HTTP_OK == urlConn
                        .getResponseCode());
            } catch (Exception e) {
                String message = Messages.get(locale,
                        "error_invalid_externalui_url",
                        new Object[] { configuratorUrl });
                log.error(message, e);
                throw new Exception(message);
            }

            if (!valid) {
                String message = Messages.get(locale,
                        "error_invalid_externalui_url",
                        new Object[] { configuratorUrl });
                log.error(message);
                throw new Exception(message);
            }
        }
    }

    private void validateScriptUrl(String locale, List<VOParameter> params)
            throws Exception {
        log.debug("");
        String url = getParamValue(VMPropertyHandler.TS_SCRIPT_URL, params);
        if (url != null && url.length() > 0) {
            log.debug("URL: " + url);
            HttpURLConnection conn = null;
            int returnErrorCode = HttpsURLConnection.HTTP_OK;
            try {
                URL urlSt = new URL(url);
                conn = (HttpURLConnection) urlSt.openConnection();
                returnErrorCode = conn.getResponseCode();
            } catch (Exception e) {
                log.error("Failed to locate script file " + url, e);
                throw new Exception(Messages.get(locale,
                        "error_invalid_script_url", new Object[] { url }));

            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            if (HttpsURLConnection.HTTP_OK != returnErrorCode) {
                throw new Exception(Messages.get(locale,
                        "error_invalid_script_url", new Object[] { url }));
            }
        }
    }

    private void validateTargetFolder(String locale, List<VOParameter> params)
            throws Exception {
        String targetFolder = getParamValue(VMPropertyHandler.TS_TARGET_FOLDER,
                params);
        String vcenter = getParamValue(
                VMPropertyHandler.TS_TARGET_VCENTER_SERVER, params);

        log.debug("targetFolder: " + targetFolder + " vcenter: " + vcenter);

        if (vcenter != null && targetFolder != null
                && targetFolder.length() > 0) {

            VMwareClientFactory vmwFactory = new VMwareClientFactory(locale);

            try (VMwareClient vmw = vmwFactory.getInstance(vcenter);) {
                vmw.connect();
                ManagedObjectReference moRefTargetFolder = vmw.getServiceUtil()
                        .getDecendentMoRef(null, "Folder", targetFolder);

                if (moRefTargetFolder == null) {
                    log.error("Target folder " + targetFolder + " not found.");
                    throw new Exception(
                            Messages.get(locale, "error_invalid_target_folder",
                                    new Object[] { targetFolder }));
                }
            }
        } else {
            log.info("Cannot validate target folder. Missing information. "
                    + " targetFolder: " + targetFolder + " vcenter: "
                    + vcenter);
        }
    }

    private void validateVLAN(String locale, List<VOParameter> params)
            throws Exception {
        String vcenter = getParamValue(
                VMPropertyHandler.TS_TARGET_VCENTER_SERVER, params);
        String datacenter = getParamValue(
                VMPropertyHandler.TS_TARGET_DATACENTER, params);
        String host = getParamValue(VMPropertyHandler.TS_TARGET_HOST, params);

        log.debug("vcenter: " + vcenter + " host: " + host);

        if (vcenter != null && host != null) {

            try (VMwareClient vmw = vmwFactory.getInstance(vcenter);) {

                vmw.connect();
                int numNICs = Integer.parseInt(getParamValue(
                        VMPropertyHandler.TS_NUMBER_OF_NICS, params));

                for (int i = 1; i <= numNICs; i++) {
                    String vlan = getParamValue("NIC" + i + "_NETWORK_ADAPTER",
                            params);
                    if (vlan != null && vlan.length() > 0) {
                        if (!doesVLANexist(vmw, datacenter, host, vlan)) {
                            throw new Exception(
                                    Messages.get(locale, "error_invalid_vlan",
                                            new Object[] { vlan }));
                        }
                    }
                }
            }

        } else {
            log.info("Cannot validate VLAN. Missing information. vcenter: "
                    + vcenter + " datacenter: " + datacenter + " host: "
                    + host);
        }
    }

    private boolean doesVLANexist(VMwareClient vmw, String datacenter,
            String host, String vlan) throws Exception {
        log.debug("datacenter: " + datacenter + "host: " + host + "vlan: "
                + vlan);
        boolean vlanExist = false;
        ManagedObjectReference dcmor = vmw.getServiceUtil()
                .getDecendentMoRef(null, "Datacenter", datacenter);

        ManagedObjectReference hostmor = vmw.getServiceUtil()
                .getDecendentMoRef(dcmor, "HostSystem", host);

        if (hostmor != null) {

            @SuppressWarnings("unchecked")
            ArrayList<ManagedObjectReference> networkRefList = (ArrayList<ManagedObjectReference>) vmw
                    .getServiceUtil().getDynamicProperty(hostmor, "network");

            for (ManagedObjectReference networkRef : networkRefList) {
                String name = (String) vmw.getServiceUtil()
                        .getDynamicProperty(networkRef, "name");
                if (name != null && name.equals(vlan)) {
                    vlanExist = true;
                    break;
                }
            }
        } else {
            throw new Exception("Host not found " + host);
        }
        return vlanExist;
    }

    private void validateInstancenamePrefix(String locale,
            List<VOParameter> params) throws Exception {
        String prefix = getParamValue(VMPropertyHandler.TS_INSTANCENAME_PREFIX,
                params);
        log.debug("prefix: " + prefix);
        if (prefix != null && prefix.length() > 0) {
            String regexp = "^[a-zA-Z0-9][a-zA-Z0-9-]{0,60}$";
            Pattern p = Pattern.compile(regexp);
            Matcher m = p.matcher(prefix);
            if (!m.matches()) {
                throw new Exception(Messages.get(locale, "error_invalid_prefix",
                        new Object[] { prefix }));
            }
        }
    }

    private void validateTemplate(String locale, List<VOParameter> params)
            throws Exception {
        String template = getParamValue(VMPropertyHandler.TS_TEMPLATENAME,
                params);
        String vcenter = getParamValue(
                VMPropertyHandler.TS_TARGET_VCENTER_SERVER, params);

        log.debug("template: " + template + " vcenter: " + vcenter);

        if (template != null && vcenter != null) {

            try (VMwareClient vmw = vmwFactory.getInstance(vcenter);) {
                vmw.connect();
                VM templateVM = new VM(vmw, template);
                String numNICs = getParamValue(
                        VMPropertyHandler.TS_NUMBER_OF_NICS, params);

                if (numNICs != null && numNICs.length() > 0) {
                    int numNics = Integer.parseInt(numNICs);
                    int numNicsTemplate = templateVM.getNumberOfNICs();
                    if (numNics != numNicsTemplate) {
                        throw new Exception(Messages.get(locale,
                                "error_template_number_nics",
                                new Object[] { numNicsTemplate, numNICs }));
                    }
                }
            }
        } else {
            log.info("Cannot validate template. Missing information. "
                    + " template: " + template + " vcenter: " + vcenter);
        }
    }

    private VOServiceDetails getServiceDetails(String supplierOrgId,
            VOService service) throws Exception {
        log.debug("serviceId: " + service.getServiceId() + " / supplierOrgId: "
                + supplierOrgId);

        ServiceProvisioningService prvSvc = (new BesClient())
                .getWebServiceAsOrganizationAdmin(
                        ServiceProvisioningService.class, supplierOrgId);
        return prvSvc.getServiceDetails(service);
    }

    private VOOrganization getOrganization(String orgId) throws Exception {
        log.debug("orgId: " + orgId);
        AccountService accountSvc = (new BesClient())
                .getWebServiceAsOrganizationAdmin(AccountService.class, orgId);
        return accountSvc.getOrganizationData();
    }

}
