/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016
 *                                                                                                                                 
 *  Creation Date: Aug 26, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.billing.external.pricemodel.service;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ejb.Remote;

import org.oscm.billing.external.context.ContextKey;
import org.oscm.billing.external.context.ContextValue;
import org.oscm.billing.external.exception.BillingException;

/**
 * Interface for price models of external billing system
 */
@Remote
public interface PriceModelPluginService {

    /**
     * Get the detailed description and the tag of the given price model from
     * the external billing system for specified OSCM context and set of
     * locales. If the set of specified locales is null or empty or if the
     * locales are not supported, the price model description and tag for the
     * default locale is returned. The price model tag is used for displaying on
     * OSCM marketplace. A length of maximum 30 bytes for the tag is expected,
     * otherwise it is not displayed.
     * 
     * @param context
     *            the OSCM context for this operation
     * @param locales
     *            set of locales
     * @return price model object containing localized description and tag or
     *         <code>null</code> if the context is empty or if the price model
     *         was not found; japanese characters have to be converted to
     *         unicode escape sequences.
     * @throws BillingException
     */
    public PriceModel getPriceModel(Map<ContextKey, ContextValue<?>> context,
            Set<Locale> locales) throws BillingException;
}
