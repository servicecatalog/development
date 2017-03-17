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

import org.oscm.internal.vo.VOEventDefinition;

public class MockVOEventDefinition extends VOEventDefinition {

    private static long nextVoKey = 1;

    private static final long serialVersionUID = 1L;

    public MockVOEventDefinition() {
        super();
        setKey(nextVoKey++);
    }

}
