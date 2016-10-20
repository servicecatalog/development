package org.oscm.rest.service;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.oscm.internal.intf.AccountService;
import org.oscm.internal.vo.VOOrganization;
import org.oscm.internal.vo.VOTechnicalService;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.service.data.OrganizationRepresentation;

@Stateless
public class TSSupplierBackend {

    @EJB
    AccountService as;

    public RestBackend.GetCollection<OrganizationRepresentation, ServiceParameters> getCollection() {
        return new RestBackend.GetCollection<OrganizationRepresentation, ServiceParameters>() {

            @Override
            public RepresentationCollection<OrganizationRepresentation> getCollection(ServiceParameters params)
                    throws Exception {
                VOTechnicalService vo = new VOTechnicalService();
                vo.setKey(params.getId().longValue());
                List<VOOrganization> list = as.getSuppliersForTechnicalService(vo);
                return OrganizationRepresentation.toCollection(list);
            }
        };
    }

    public RestBackend.Post<OrganizationRepresentation, ServiceParameters> post() {
        return new RestBackend.Post<OrganizationRepresentation, ServiceParameters>() {

            @Override
            public Object post(OrganizationRepresentation content, ServiceParameters params) throws Exception {
                VOTechnicalService vo = new VOTechnicalService();
                vo.setKey(params.getId().longValue());
                as.addSuppliersForTechnicalService(vo, Arrays.asList(content.getOrganizationId()));
                return content.getOrganizationId();
            }
        };
    }

    public RestBackend.Delete<ServiceParameters> delete() {
        return new RestBackend.Delete<ServiceParameters>() {

            @Override
            public boolean delete(ServiceParameters params) throws Exception {
                VOTechnicalService vo = new VOTechnicalService();
                vo.setKey(params.getId().longValue());
                as.removeSuppliersFromTechnicalService(vo, Arrays.asList(params.getOrgId()));
                return true;
            }
        };
    }
}
