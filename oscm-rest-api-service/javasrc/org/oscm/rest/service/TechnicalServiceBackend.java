package org.oscm.rest.service;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oscm.domobjects.TechnicalProduct;
import org.oscm.internal.intf.ServiceProvisioningService;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.vo.VOTechnicalService;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.service.data.TechnicalServiceRepresentation;

@Stateless
@LocalBean
public class TechnicalServiceBackend implements RestBackend.Post<TechnicalServiceRepresentation, ServiceParameters>,
        RestBackend.Delete<ServiceParameters>,
        RestBackend.GetCollection<TechnicalServiceRepresentation, ServiceParameters> {

    @EJB
    ServiceProvisioningService sps;

    @PersistenceContext(name = "persistence/em", unitName = "oscm-domainobjects")
    protected EntityManager em;

    @Override
    public RepresentationCollection<TechnicalServiceRepresentation> getCollection(ServiceParameters params)
            throws Exception {
        // TODO: role by query parameter?
        List<VOTechnicalService> technicalServices = sps.getTechnicalServices(OrganizationRoleType.TECHNOLOGY_PROVIDER);
        Collection<TechnicalServiceRepresentation> list = TechnicalServiceRepresentation
                .toCollection(technicalServices);
        return new RepresentationCollection<>(list);
    }

    @Override
    public void delete(ServiceParameters params) throws Exception {
        TechnicalProduct tp = em.getReference(TechnicalProduct.class, params.getId().longValue());
        VOTechnicalService ts = new VOTechnicalService();
        ts.setKey(tp.getKey());
        ts.setVersion(tp.getVersion());
        // TODO: this checks the whole structure and won't work this way :(
        sps.deleteTechnicalService(ts);
    }

    @Override
    public Object post(TechnicalServiceRepresentation content, ServiceParameters params) throws Exception {
        VOTechnicalService ts = sps.createTechnicalService(content.getVO());
        return Long.valueOf(ts.getKey());
    }

}
