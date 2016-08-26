package org.oscm.rest.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oscm.domobjects.Product;
import org.oscm.internal.intf.ServiceProvisioningService;
import org.oscm.internal.types.exception.DomainObjectException.ClassEnum;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOServiceDetails;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.service.data.ServiceDetailsRepresentation;
import org.oscm.rest.service.data.ServiceRepresentation;

@Stateless
@LocalBean
public class ServiceBackend implements RestBackend.Get<ServiceDetailsRepresentation, ServiceParameters>,
        RestBackend.Put<ServiceDetailsRepresentation, ServiceParameters>,
        RestBackend.Post<ServiceDetailsRepresentation, ServiceParameters>, RestBackend.Delete<ServiceParameters>,
        RestBackend.GetCollection<ServiceRepresentation, ServiceParameters> {

    @EJB
    ServiceProvisioningService sps;

    @PersistenceContext(name = "persistence/em", unitName = "oscm-domainobjects")
    protected EntityManager em;

    @Override
    public void delete(ServiceParameters params) throws Exception {
        Product p = em.getReference(Product.class, params.getId().longValue());
        VOService vo = new VOService();
        vo.setKey(params.getId().longValue());
        vo.setVersion(p.getVersion());
        sps.deleteService(vo);
    }

    @Override
    public Object post(ServiceDetailsRepresentation content, ServiceParameters params) throws Exception {
        // image will be handled in separate URL
        VOServiceDetails vo = sps.createService(content.getTechnicalService().getVO(), content.getVO(), null);
        return Long.valueOf(vo.getKey());
    }

    @Override
    public void put(ServiceDetailsRepresentation content, ServiceParameters params) throws Exception {
        // image will be handled in separate URL
        sps.updateService(content.getVO(), null);
    }

    @Override
    public ServiceDetailsRepresentation get(ServiceParameters params) throws Exception {
        VOService vo = new VOService();
        vo.setKey(params.getId().longValue());
        VOServiceDetails sd = sps.getServiceDetails(vo);
        if (sd == null) {
            throw new ObjectNotFoundException(ClassEnum.SERVICE, String.valueOf(vo.getKey()));
        }
        return new ServiceDetailsRepresentation(sd);
    }

    @Override
    public RepresentationCollection<ServiceRepresentation> getCollection(ServiceParameters params) throws Exception {
        List<VOService> list = sps.getSuppliedServices();
        return new RepresentationCollection<ServiceRepresentation>(ServiceRepresentation.toCollection(list));
    }

}
