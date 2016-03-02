/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                  
 *  Creation Date: 22.02.2016 15:05
 *
 *******************************************************************************/

package org.oscm.converter.strategy.api;

import java.util.List;

import org.oscm.converter.api.Converter;
import org.oscm.converter.strategy.ConversionStrategy;
import org.oscm.domobjects.ParameterDefinition;
import org.oscm.domobjects.ParameterOption;
import org.oscm.types.enumtypes.ParameterModificationType;
import org.oscm.types.enumtypes.ParameterType;
import org.oscm.types.enumtypes.ParameterValueType;
import org.oscm.vo.VOParameterDefinition;
import org.oscm.vo.VOParameterOption;

public class ToExtParameterDefinitionStrategy implements ConversionStrategy<ParameterDefinition, VOParameterDefinition> {
//description
    @Override
    public VOParameterDefinition convert(ParameterDefinition parameterDefinition) {
        if (parameterDefinition == null) {
            return null;
        }

        VOParameterDefinition voParameterDefinition = new VOParameterDefinition();

        voParameterDefinition.setVersion(parameterDefinition.getVersion());
        voParameterDefinition.setKey(parameterDefinition.getKey());
        voParameterDefinition.setDefaultValue(parameterDefinition.getDefaultValue());
        voParameterDefinition.setMandatory(parameterDefinition.isMandatory());
        voParameterDefinition.setConfigurable(parameterDefinition.isConfigurable());
        voParameterDefinition.setMaxValue(parameterDefinition.getMaximumValue());
        voParameterDefinition.setMinValue(parameterDefinition.getMinimumValue());
        voParameterDefinition.setModificationType(ParameterModificationType.valueOf(parameterDefinition.getModificationType().name()));
        voParameterDefinition.setParameterId(parameterDefinition.getParameterId());
        List<ParameterOption> parameterOptions = parameterDefinition.getOptionList();
        List<VOParameterOption> voParameterOptions = Converter.convertList(parameterOptions, ParameterOption.class, VOParameterOption.class);
        voParameterDefinition.setParameterOptions(voParameterOptions);
        voParameterDefinition.setParameterType(ParameterType.valueOf(parameterDefinition.getParameterType().name()));
        voParameterDefinition.setValueType(ParameterValueType.valueOf(parameterDefinition.getValueType().name()));

        return voParameterDefinition;
    }

}
