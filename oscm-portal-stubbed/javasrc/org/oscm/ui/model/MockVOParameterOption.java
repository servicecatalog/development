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

import org.oscm.internal.vo.VOParameterOption;

public class MockVOParameterOption extends VOParameterOption {

    private static long nextVoKey = 1;

    private static final long serialVersionUID = 1L;

    public MockVOParameterOption(String optionId, String optionDescription,
            String paramDefId) {
        super(optionId, optionDescription, paramDefId);
        setKey(nextVoKey++);
    }

}
