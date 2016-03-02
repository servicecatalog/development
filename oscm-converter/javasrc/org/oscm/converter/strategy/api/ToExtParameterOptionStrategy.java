/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                  
 *  Creation Date: 22.02.2016 15:57
 *
 *******************************************************************************/

package org.oscm.converter.strategy.api;

import org.oscm.converter.strategy.ConversionStrategy;
import org.oscm.domobjects.ParameterOption;
import org.oscm.vo.VOParameterOption;

public class ToExtParameterOptionStrategy implements ConversionStrategy<ParameterOption, VOParameterOption> {
//optionDescription, paramDefId
    @Override
    public VOParameterOption convert(ParameterOption parameterOption) {
        if (parameterOption == null) {
            return null;
        }

        VOParameterOption voParameterDefinition = new VOParameterOption();

        voParameterDefinition.setKey(parameterOption.getKey());
        voParameterDefinition.setOptionId(parameterOption.getOptionId());
        voParameterDefinition.setVersion(parameterOption.getVersion());

        return voParameterDefinition;
    }

}
