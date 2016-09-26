package org.oscm.rest.service;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.oscm.internal.intf.ServiceProvisioningService;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.vo.VOTechnicalService;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.service.data.TechnicalServiceRepresentation;

@Stateless
public class TechnicalServiceBackend {

    @EJB
    ServiceProvisioningService sps;

    public RestBackend.GetCollection<TechnicalServiceRepresentation, ServiceParameters> getCollection() {
        return new RestBackend.GetCollection<TechnicalServiceRepresentation, ServiceParameters>() {

            @Override
            public RepresentationCollection<TechnicalServiceRepresentation> getCollection(ServiceParameters params)
                    throws Exception {
                // TODO: role by query parameter?
                List<VOTechnicalService> technicalServices = sps
                        .getTechnicalServices(OrganizationRoleType.TECHNOLOGY_PROVIDER);
                Collection<TechnicalServiceRepresentation> list = TechnicalServiceRepresentation
                        .toCollection(technicalServices);
                return new RepresentationCollection<>(list);
            }
        };
    }

    public RestBackend.Delete<ServiceParameters> delete() {
        return new RestBackend.Delete<ServiceParameters>() {

            @Override
            public boolean delete(ServiceParameters params) throws Exception {
                sps.deleteTechnicalService(params.getId());
                return true;
            }
        };
    }

    public RestBackend.Post<TechnicalServiceRepresentation, ServiceParameters> post() {
        return new RestBackend.Post<TechnicalServiceRepresentation, ServiceParameters>() {

            @Override
            public Object post(TechnicalServiceRepresentation content, ServiceParameters params) throws Exception {
                VOTechnicalService ts = sps.createTechnicalService(content.getVO());
                return Long.valueOf(ts.getKey());
            }
        };
    }

}
