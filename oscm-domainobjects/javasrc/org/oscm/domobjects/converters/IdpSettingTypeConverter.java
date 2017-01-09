/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016                                  
 *                                                                               
 *  Creation Date: 04.01.17
 *    
 ******************************************************************************/

package org.oscm.domobjects.converters;

import javax.persistence.AttributeConverter;

import org.oscm.internal.types.enumtypes.IdpSettingType;

public class IdpSettingTypeConverter implements AttributeConverter<IdpSettingType, String>{

    @Override
    public String convertToDatabaseColumn(IdpSettingType idpSettingType) {
        return idpSettingType.name();
    }

    @Override
    public IdpSettingType convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        return IdpSettingType.valueOf(s);
    }

}
