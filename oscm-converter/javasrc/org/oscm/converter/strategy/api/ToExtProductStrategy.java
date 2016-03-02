/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                  
 *  Creation Date: 22.07.15 13:39
 *
 *******************************************************************************/

package org.oscm.converter.strategy.api;

import java.util.List;

import org.oscm.converter.api.Converter;
import org.oscm.converter.api.EnumConverter;
import org.oscm.converter.strategy.ConversionStrategy;
import org.oscm.domobjects.Parameter;
import org.oscm.domobjects.PriceModel;
import org.oscm.domobjects.Product;
import org.oscm.internal.types.enumtypes.ServiceStatus;
import org.oscm.types.enumtypes.ServiceAccessType;
import org.oscm.vo.VOParameter;
import org.oscm.vo.VOPriceModel;
import org.oscm.vo.VOService;
public class ToExtProductStrategy implements ConversionStrategy<Product, VOService> {

    @Override
    public VOService convert(Product product) {
        if (product == null) {
            return null;
        }
//description, shortdescription, tags, offeringType
        VOService voService = new VOService();

        voService.setKey(product.getKey());
        voService.setVersion(product.getVersion());
        voService.setName(product.getProductId());
        voService.setConfiguratorUrl(product.getConfiguratorUrl());
        VOPriceModel voPriceModel = Converter.convert(product.getPriceModel(), PriceModel.class, VOPriceModel.class);
        voService.setPriceModel(voPriceModel);
        if (product.getParameterSet() != null) {
            List<VOParameter> voParameters = Converter.convertList(product.getParameterSet().getParameters(), Parameter.class, VOParameter.class);
            voService.setParameters(voParameters);
        }
        if (product.getProductFeedback() != null) {
            voService.setAverageRating(product.getProductFeedback().getAverageRating());
            voService.setNumberOfReviews(product.getProductFeedback().getProductReviews().size());
        }
        voService.setBaseURL(product.getTechnicalProduct().getBaseURL());
        voService.setAccessType(ServiceAccessType.valueOf(product.getTechnicalProduct().getAccessType().name()));
        voService.setSellerId(product.getVendor().getOrganizationId());
        voService.setSellerKey(product.getVendorKey());
        voService.setSellerName(product.getVendor().getName());
        ServiceStatus serviceStatus = ServiceStatus.valueOf(product.getStatus().name());
        org.oscm.types.enumtypes.ServiceStatus voServicestatus = EnumConverter.convert(serviceStatus, org.oscm.types.enumtypes.ServiceStatus.class);
        voService.setStatus(voServicestatus);
        voService.setTechnicalId(product.getTechnicalProduct().getTechnicalProductId());
        voService.setServiceId(product.getCleanProductId());
        voService.setFeatureURL(product.getTechnicalProduct().getProvisioningURL());
        return voService;
    }
}
