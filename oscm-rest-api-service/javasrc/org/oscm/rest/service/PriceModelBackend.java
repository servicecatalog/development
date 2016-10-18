package org.oscm.rest.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.oscm.internal.intf.ServiceProvisioningService;
import org.oscm.internal.types.exception.DomainObjectException.ClassEnum;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOOrganization;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOServiceDetails;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.service.data.PriceModelRepresentation;

@Stateless
public class PriceModelBackend {

    @EJB
    ServiceProvisioningService sps;

    public RestBackend.Put<PriceModelRepresentation, ServiceParameters> put() {
        return new RestBackend.Put<PriceModelRepresentation, ServiceParameters>() {

            @Override
            public boolean put(PriceModelRepresentation content, ServiceParameters params) throws Exception {
                VOServiceDetails svc = new VOServiceDetails();
                svc.setKey(params.getId().longValue());
                sps.savePriceModel(svc, content.getVO());
                return true;
            }
        };
    }

    public RestBackend.Put<PriceModelRepresentation, ServiceParameters> putForCustomer() {
        return new RestBackend.Put<PriceModelRepresentation, ServiceParameters>() {

            @Override
            public boolean put(PriceModelRepresentation content, ServiceParameters params) throws Exception {
                VOServiceDetails svc = new VOServiceDetails();
                svc.setKey(params.getId().longValue());
                VOOrganization cust = new VOOrganization();
                cust.setKey(params.getOrgKey().longValue());
                sps.savePriceModelForCustomer(svc, content.getVO(), cust);
                return true;
            }
        };
    }

    public RestBackend.Get<PriceModelRepresentation, ServiceParameters> get() {
        return new RestBackend.Get<PriceModelRepresentation, ServiceParameters>() {

            @Override
            public PriceModelRepresentation get(ServiceParameters params) throws Exception {
                VOService vo = new VOService();
                vo.setKey(params.getId().longValue());
                VOServiceDetails sd = sps.getServiceDetails(vo);
                if (sd == null) {
                    throw new ObjectNotFoundException(ClassEnum.SERVICE, String.valueOf(vo.getKey()));
                }
                return new PriceModelRepresentation(sd.getPriceModel());
            }
        };
    }

    public RestBackend.Get<PriceModelRepresentation, ServiceParameters> getForCustomer() {
        return new RestBackend.Get<PriceModelRepresentation, ServiceParameters>() {

            @Override
            public PriceModelRepresentation get(ServiceParameters params) throws Exception {
                VOService svc = new VOService();
                svc.setKey(params.getId().longValue());
                VOOrganization cust = new VOOrganization();
                cust.setKey(params.getOrgKey().longValue());
                VOServiceDetails sd = sps.getServiceForCustomer(cust, svc);
                if (sd == null) {
                    throw new ObjectNotFoundException(ClassEnum.SERVICE, String.valueOf(svc.getKey()));
                }
                return new PriceModelRepresentation(sd.getPriceModel());
            }
        };
    }

}
