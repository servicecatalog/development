/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 2016-05-24
 *
 *******************************************************************************/

package org.oscm.app.vmware.remote.bes;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.oscm.app.vmware.business.VMPropertyHandler;
import org.oscm.app.vmware.persistence.APPDataAccessService;
import org.oscm.intf.ServiceProvisioningService;
import org.oscm.intf.SubscriptionService;
import org.oscm.vo.VOParameter;
import org.oscm.vo.VOParameterDefinition;
import org.oscm.vo.VOService;
import org.oscm.vo.VOServiceDetails;
import org.oscm.vo.VOSubscriptionDetails;
import org.oscm.vo.VOTechnicalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceParamRetrieval {

    private static final Logger logger = LoggerFactory
            .getLogger(ServiceParamRetrieval.class);

    private VOServiceDetails service;
    private VMPropertyHandler ph;

    public ServiceParamRetrieval(VMPropertyHandler ph) {
        this.ph = ph;
        String customerOrgId = ph.getSettings().getOrganizationId();
        logger.debug("customerOrgId: " + customerOrgId);

        try {
            BesClient bes = new BesClient();
            Credentials credentials = ph.getTPUser();
            SubscriptionService subSvc = bes
                    .getWebService(SubscriptionService.class, credentials);

            VOSubscriptionDetails subscr = subSvc.getSubscriptionForCustomer(
                    customerOrgId, ph.getSettings().getSubscriptionId());

            APPDataAccessService das = new APPDataAccessService();
            credentials = das.getCredentials(
                    subscr.getSubscribedService().getSellerId());
            ServiceProvisioningService provSvc = bes.getWebService(
                    ServiceProvisioningService.class, credentials);
            VOService svc = subscr.getSubscribedService();
            service = provSvc.getServiceDetails(svc);
        } catch (Exception e) {
            logger.error("Failed to initialize ServiceParameter. customerOrg: "
                    + customerOrgId, e);
        }
    }

    public String getServiceSetting(String parameterId) throws Exception {
        logger.debug("parameter: " + parameterId);
        String value = ph.getServiceSetting(parameterId);
        if (value != null) {
            logger.debug("found parameter in parameter list. " + parameterId
                    + ": " + value);
            return value;
        }

        List<VOParameter> serviceParams = service.getParameters();
        for (VOParameter p : serviceParams) {
            if (p.getParameterDefinition().getParameterId()
                    .equals(parameterId)) {
                logger.debug("found parameter in marketable service. "
                        + parameterId + ": " + p.getValue());
                return p.getValue();
            }
        }

        List<VOParameterDefinition> techServiceParams = service
                .getTechnicalService().getParameterDefinitions();
        for (VOParameterDefinition p : techServiceParams) {
            if (p.getParameterId().equals(parameterId)) {
                logger.debug("found parameter in technical service. "
                        + parameterId + ": " + p.getDefaultValue());
                return p.getDefaultValue();
            }
        }

        BesClient bes = new BesClient();
        ServiceProvisioningService sps = bes.getWebService(
                ServiceProvisioningService.class, ph.getTPUser());
        List<VOTechnicalService> technicalServices = new ArrayList<VOTechnicalService>();
        technicalServices.add(service.getTechnicalService());
        byte[] tsvc = sps.exportTechnicalServices(technicalServices);
        InputStream in = new ByteArrayInputStream(tsvc);
        XMLConfiguration xml = new XMLConfiguration();
        xml.setSchemaValidation(false);
        xml.setExpressionEngine(new XPathExpressionEngine());
        xml.load(in);

        List<HierarchicalConfiguration> params = xml.configurationsAt(
                "//ParameterDefinition[@configurable=\"false\"]");
        for (HierarchicalConfiguration param : params) {
            if (param.getString("@id").equals(parameterId)) {
                return param.getString("@default");
            }
        }

        throw new Exception(
                "Failed to retrieve service parameter " + parameterId);
    }

}
