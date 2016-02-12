/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 07.08.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.billing.external.context;

import java.io.Serializable;

public class ContextValueString extends ContextValue<String> implements
        Serializable {

    private static final long serialVersionUID = -7405786827911921276L;

    public ContextValueString(String value) {
        super(value);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

}
