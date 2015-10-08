/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2015                                           
 *                                                                                                                                  
 *  Creation Date: 21.07.15 16:14
 *
 *******************************************************************************/

package org.oscm.converter.strategy.domain;

import org.oscm.domobjects.UserGroup;
import org.oscm.converter.strategy.ConversionStrategy;
import org.oscm.vo.VOOrganizationalUnit;

public class ToDomUnitStrategy implements
        ConversionStrategy<VOOrganizationalUnit, UserGroup> {

    /**
     * Only keys should be set on (@link VOOrganizationalUnit) current object
     * should be retrieved from DB.
     * 
     * @param organizationalUnit - VOOrganizationalUnit
     * @return - UserGroup converted from VOOrganizationalUnit
     */
    @Override
    public UserGroup convert(VOOrganizationalUnit organizationalUnit) {
        if(organizationalUnit == null) {
            return null;
        }
        
        UserGroup userGroup = new UserGroup();

        userGroup.setName(organizationalUnit.getName());
        userGroup.setDescription(organizationalUnit.getDescription());
        userGroup.setReferenceId(organizationalUnit.getReferenceId());
        userGroup.setKey(organizationalUnit.getKey());
        return userGroup;
    }
}
