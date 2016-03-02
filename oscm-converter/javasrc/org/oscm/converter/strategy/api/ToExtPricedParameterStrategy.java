/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                  
 *  Creation Date: 22.02.2016 14:58
 *
 *******************************************************************************/

package org.oscm.converter.strategy.api;

import java.util.List;

import org.oscm.converter.api.Converter;
import org.oscm.converter.strategy.ConversionStrategy;
import org.oscm.domobjects.ParameterDefinition;
import org.oscm.domobjects.PricedOption;
import org.oscm.domobjects.PricedParameter;
import org.oscm.domobjects.PricedProductRole;
import org.oscm.vo.VOParameterDefinition;
import org.oscm.vo.VOPricedOption;
import org.oscm.vo.VOPricedParameter;
import org.oscm.vo.VOPricedRole;

public class ToExtPricedParameterStrategy implements ConversionStrategy<PricedParameter, VOPricedParameter> {

    @Override
    public VOPricedParameter convert(PricedParameter pricedParameter) {
        if (pricedParameter == null) {
            return null;
        }

        VOPricedParameter voPricedParameter = new VOPricedParameter();

        voPricedParameter.setKey(pricedParameter.getKey());
        voPricedParameter.setVersion(pricedParameter.getVersion());
        if (pricedParameter.getParameter() != null) {
            voPricedParameter.setParameterKey(pricedParameter.getParameter().getKey());
        }
        List<PricedOption> pricedOptionList = pricedParameter.getPricedOptionList();
        List<VOPricedOption> voPricedOptions = Converter.convertList(pricedOptionList, PricedOption.class, VOPricedOption.class);
        voPricedParameter.setPricedOptions(voPricedOptions);
        voPricedParameter.setPricePerSubscription(pricedParameter.getPricePerSubscription());
        voPricedParameter.setPricePerUser(pricedParameter.getPricePerUser());
        List<PricedProductRole> roleSpecificUserPrices = pricedParameter.getRoleSpecificUserPrices();
        List<VOPricedRole> voRoleSpecificUserPrices = Converter.convertList(roleSpecificUserPrices, PricedProductRole.class, VOPricedRole.class);
        voPricedParameter.setRoleSpecificUserPrices(voRoleSpecificUserPrices);
        ParameterDefinition parameterDefinition = pricedParameter.getParameter().getParameterDefinition();
        VOParameterDefinition voParameterDefinition = Converter.convert(parameterDefinition, ParameterDefinition.class, VOParameterDefinition.class);
        voPricedParameter.setVoParameterDef(voParameterDefinition);
        return voPricedParameter;
    }

}
