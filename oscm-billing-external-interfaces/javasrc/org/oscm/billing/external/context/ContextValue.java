/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                           
 *                                                                                                                                 
 *  Creation Date: 07.08.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.billing.external.context;

import java.io.Serializable;

/**
 * @author baumann
 *
 */
public abstract class ContextValue<T> implements Serializable {

    private static final long serialVersionUID = 4738410026389162268L;

    protected T value;

    public ContextValue(T value) {
        this.value = value;
    }

    /**
     * @return the context value
     */
    public abstract T getValue();

    /**
     * @param value
     *            the context value to set
     */
    public abstract void setValue(T value);

}
