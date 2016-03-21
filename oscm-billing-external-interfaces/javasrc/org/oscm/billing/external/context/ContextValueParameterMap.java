/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 07.08.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.billing.external.context;

import java.io.Serializable;
import java.util.Map;

public class ContextValueParameterMap extends ContextValue<Map<String, String>>
        implements Serializable {

    private static final long serialVersionUID = -5963298259926778603L;

    public ContextValueParameterMap(Map<String, String> value) {
        super(value);
    }

    @Override
    public Map<String, String> getValue() {
        return value;
    }

    @Override
    public void setValue(Map<String, String> value) {
        this.value = value;
    }

}
