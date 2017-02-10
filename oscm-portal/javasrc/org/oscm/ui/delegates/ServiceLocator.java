/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: Jun 5, 2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.delegates;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.oscm.internal.types.exception.SaaSSystemException;

public class ServiceLocator {

    public <T> T findService(final Class<T> clazz) {

        try {
            Context context = new InitialContext();
            T service = clazz.cast(context.lookup(clazz.getName()));
            return service;
        } catch (NamingException e) {
            throw new SaaSSystemException("Service lookup failed!", e);
        }
    }

}
