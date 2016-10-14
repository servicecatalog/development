/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Oct 12, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.applicationservice.filter;

import java.util.ArrayList;
import java.util.List;

import org.oscm.domobjects.Organization;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.Uda;
import org.oscm.domobjects.UdaDefinition;
import org.oscm.provisioning.data.ServiceAttribute;
import org.oscm.types.enumtypes.UdaTargetType;

/**
 * Filters and converts the UDAs of an Subscription
 * 
 * @author miethaner
 */
public class AttributeFilter {

    /**
     * Converts the UDA list of the subscription related organization into a
     * ServiceAttribute list.
     * 
     * @param parameterSet
     *            The ParameterSet of parameters which have to be converted.
     * @param technicalProduct
     *            The related technical product the
     * @param filterOnetimeParameter
     *            To filter out the product parameters with ONE_TIME
     *            modificationType
     * 
     * @return the created ServiceAttribute list.
     */
    public static List<ServiceAttribute> getServiceParameterList(
            Subscription subscription) {

        ArrayList<ServiceAttribute> list = new ArrayList<>();

        Organization org = subscription.getProduct().getVendor();

        for (UdaDefinition def : org.getUdaDefinitions()) {
            if (def.getTargetType() == UdaTargetType.CUSTOMER) {
                boolean exists = false;

                for (Uda uda : def.getUdas()) {
                    if (uda.getTargetObjectKey() == org.getKey()) {
                        ServiceAttribute attr = new ServiceAttribute();
                        attr.setAttributeId(def.getUdaId());
                        attr.setValue(uda.getUdaValue());
                        list.add(attr);
                        exists = true;
                    }
                }

                if (!exists) {
                    ServiceAttribute attr = new ServiceAttribute();
                    attr.setAttributeId(def.getUdaId());
                    attr.setValue(def.getDefaultValue());
                    list.add(attr);
                }
            }
        }

        return list;
    }

}
