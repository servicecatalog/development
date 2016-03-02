/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                  
 *  Creation Date: 22.02.2016 13:52
 *
 *******************************************************************************/

package org.oscm.converter.strategy.api;

import org.oscm.converter.strategy.ConversionStrategy;
import org.oscm.domobjects.RoleDefinition;
import org.oscm.vo.VORoleDefinition;

public class ToExtRoleDefinitionStrategy implements ConversionStrategy<RoleDefinition, VORoleDefinition> {
//description,name
    @Override
    public VORoleDefinition convert(RoleDefinition roleDefinition) {
        if (roleDefinition == null) {
            return null;
        }

        VORoleDefinition voPricedRoleDefinition = new VORoleDefinition();

        voPricedRoleDefinition.setKey(roleDefinition.getKey());
        voPricedRoleDefinition.setVersion(roleDefinition.getVersion());
        voPricedRoleDefinition.setRoleId(roleDefinition.getRoleId());
        return voPricedRoleDefinition;
    }

}
