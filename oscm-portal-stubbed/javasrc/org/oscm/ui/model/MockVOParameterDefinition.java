/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: pock                                        
 *                                                                              
 *  Creation Date: Mai 18, 2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.model;

import java.util.List;

import org.oscm.internal.types.enumtypes.ParameterType;
import org.oscm.internal.types.enumtypes.ParameterValueType;
import org.oscm.internal.vo.VOParameterDefinition;
import org.oscm.internal.vo.VOParameterOption;

public class MockVOParameterDefinition extends VOParameterDefinition {

    private static long nextVoKey = 1;

    private static final long serialVersionUID = 1L;

    public MockVOParameterDefinition(ParameterType parameterType,
            String parameterId, String description,
            ParameterValueType valueType, String defaultValue, Long minValue,
            Long maxValue, boolean mandatory, boolean configurable,
            List<VOParameterOption> parameterOptions) {
        super(parameterType, parameterId, description, valueType, defaultValue,
                minValue, maxValue, mandatory, configurable, parameterOptions);
        setKey(nextVoKey++);
    }

}
