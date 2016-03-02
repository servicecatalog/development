/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                  
 *  Creation Date: 22.02.2016 13:52
 *
 *******************************************************************************/

package org.oscm.converter.strategy.api;

import java.util.List;

import org.oscm.converter.api.Converter;
import org.oscm.converter.strategy.ConversionStrategy;
import org.oscm.domobjects.PriceModel;
import org.oscm.domobjects.PricedEvent;
import org.oscm.domobjects.PricedParameter;
import org.oscm.domobjects.PricedProductRole;
import org.oscm.domobjects.SteppedPrice;
import org.oscm.types.enumtypes.PriceModelType;
import org.oscm.types.enumtypes.PricingPeriod;
import org.oscm.vo.VOEventDefinition;
import org.oscm.vo.VOPriceModel;
import org.oscm.vo.VOPricedEvent;
import org.oscm.vo.VOPricedParameter;
import org.oscm.vo.VOPricedRole;
import org.oscm.vo.VOSteppedPrice;

public class ToExtPricedEventStrategy implements ConversionStrategy<PricedEvent, VOPricedEvent> {
//eventDefinition
    @Override
    public VOPricedEvent convert(PricedEvent pricedEvent) {
        if (pricedEvent == null) {
            return null;
        }

        VOPricedEvent voPricedEvent = new VOPricedEvent();

        voPricedEvent.setKey(pricedEvent.getKey());
        voPricedEvent.setVersion(pricedEvent.getVersion());
        List<SteppedPrice> steppedPrices = pricedEvent.getSteppedPrices();
        List<VOSteppedPrice> voSteppedPrices = Converter.convertList(steppedPrices, SteppedPrice.class, VOSteppedPrice.class);
        voPricedEvent.setSteppedPrices(voSteppedPrices);
        voPricedEvent.setEventPrice(pricedEvent.getEventPrice());

        return voPricedEvent;
    }

}
