/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015          
 *                                                                                                                                 
 *  Creation Date: 10.12.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.billing.external.pricemodel.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.oscm.billing.external.context.ContextKey;
import org.oscm.billing.external.context.ContextValue;

/**
 * Localized descriptions and tags of an external price model
 * 
 * @TODO replace implementation with Interface & move implementation to
 *       oscm-file-billing-adapter and oscm-portal
 */
public class PriceModel implements Serializable {

    private static final long serialVersionUID = 630996128479608150L;

    private UUID id;
    private Map<ContextKey, ContextValue<?>> context;
    private Map<Locale, PriceModelContent> localizedPriceModelContent = new HashMap<Locale, PriceModelContent>();

    public PriceModel(UUID id) {
        this.id = id;
    }

    /**
     * Get the unique identifier of the price model
     * 
     * @return the UUID of the price model
     */
    public UUID getId() {
        return id;
    }

    /**
     * Set the unique identifier of the price model
     * 
     * @param id
     *            an UUID
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return the context
     */
    public Map<ContextKey, ContextValue<?>> getContext() {
        return context;
    }

    /**
     * Set the context
     * 
     * @param context
     *            the context to set
     */
    public void setContext(Map<ContextKey, ContextValue<?>> context) {
        this.context = context;
    }

    /**
     * Adds or updates a localized price model content for a given locale
     * 
     * @param locale
     *            a locale
     * @param content
     *            a price model content
     * @throws NullPointerException
     *             if the specified locale or description is null
     */
    public void put(Locale locale, PriceModelContent content) {
        localizedPriceModelContent.put(locale, content);
    }

    /**
     * Get all available locales
     * 
     * @return a set of locales
     */
    public Set<Locale> getLocales() {
        return localizedPriceModelContent.keySet();
    }

    /**
     * Get the price model content for the given locale
     * 
     * @param locale
     *            a locale
     * @return the price model content; null if no content is available for the
     *         specified locale
     */
    public PriceModelContent get(Locale locale) {
        return localizedPriceModelContent.get(locale);
    }
}
