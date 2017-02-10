/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: pock                                        
 *                                                                              
 *  Creation Date: May 21, 2010                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.ui.model;

import org.oscm.internal.vo.VOParameter;
import org.oscm.internal.vo.VOParameterDefinition;

public class MockVOParameter extends VOParameter {

    private static long nextVoKey = 1;

    private static final long serialVersionUID = 1L;

    public MockVOParameter(VOParameterDefinition paramDef) {
        super(paramDef);
        setKey(nextVoKey++);
    }

}
