package org.oscm.rest.service.data;

import org.oscm.internal.vo.VOServiceDetails;

public class ServiceDetailsRepresentation extends ServiceRepresentation {

    private TechnicalServiceRepresentation technicalService;
    private boolean imageDefined;

    private transient VOServiceDetails vo;

    public ServiceDetailsRepresentation() {
        this(new VOServiceDetails());
    }

    public ServiceDetailsRepresentation(VOServiceDetails sd) {
        super(sd);
        vo = sd;
    }

    @Override
    public void update() {
        super.update();
        vo.setImageDefined(isImageDefined());
        if (technicalService != null) {
            technicalService.update();
            vo.setTechnicalService(technicalService.getVO());
        }
    }

    @Override
    public void convert() {
        super.convert();
        setImageDefined(vo.isImageDefined());
        TechnicalServiceRepresentation tsr = new TechnicalServiceRepresentation(vo.getTechnicalService());
        tsr.convert();
        setTechnicalService(tsr);
    }

    public VOServiceDetails getVO() {
        return vo;
    }

    public TechnicalServiceRepresentation getTechnicalService() {
        return technicalService;
    }

    public void setTechnicalService(TechnicalServiceRepresentation technicalService) {
        this.technicalService = technicalService;
    }

    public boolean isImageDefined() {
        return imageDefined;
    }

    public void setImageDefined(boolean imageDefined) {
        this.imageDefined = imageDefined;
    }

}
