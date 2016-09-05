/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016                                        
 *                                                                               
 *  Creation Date: 29.08.16 08:55
 *    
 ******************************************************************************/

package org.oscm.domobjects.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.oscm.internal.types.enumtypes.TriggerTargetType;

/**
 * Authored by dawidch
 */
@Converter
public class TTTConverter
        implements AttributeConverter<TriggerTargetType, String> {

    @Override
    public TriggerTargetType convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        return TriggerTargetType.valueOf(s);
    }

    @Override
    public String convertToDatabaseColumn(TriggerTargetType enumik) {
        return enumik.name();
    }
}
