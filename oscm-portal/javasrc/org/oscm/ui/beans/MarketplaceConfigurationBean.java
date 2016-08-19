/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: Zou                                                
 *                                                                              
 *  Creation Date: 14.03.2012                                                      
 *              
 *******************************************************************************/

package org.oscm.ui.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.servlet.http.HttpServletRequest;

import org.oscm.internal.intf.MarketplaceService;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOMarketplace;
import org.oscm.internal.vo.VOOrganization;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.ui.common.JSFUtils;
import org.oscm.ui.common.ServiceAccess;
import org.oscm.ui.model.MarketplaceConfiguration;

/**
 * Application scope bean for caching marketplace configurations.
 * 
 * @author Zou
 * 
 */
@ApplicationScoped
@ManagedBean(name = "marketplaceConfigurationBean")
public class MarketplaceConfigurationBean implements Serializable {

    private static final long serialVersionUID = -3521386101907735868L;

    /** Caching marketplace configurations */
    private Map<String, MarketplaceConfiguration> configurationCache = new HashMap<String, MarketplaceConfiguration>();

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(MarketplaceConfigurationBean.class);

    /**
     * MarketspaceService service instance. As member used for JUint for
     * stubbing.
     */
    private transient MarketplaceService marketplaceService = null;

    /**
     * @return the marketspaceService
     */
    protected MarketplaceService getMarketplaceService(
            HttpServletRequest request) {
        if (marketplaceService == null) {
            // Get MarketplaceService from Session
            if (request != null) {
                marketplaceService = ServiceAccess.getServiceAcccessFor(
                        request.getSession()).getService(
                        MarketplaceService.class);
            } else {
                marketplaceService = ServiceAccess.getServiceAcccessFor(
                        JSFUtils.getRequest().getSession()).getService(
                        MarketplaceService.class);
            }
        }
        return marketplaceService;
    }

    protected MarketplaceService getMarketplaceService() {
        return getMarketplaceService(null);
    }

    /**
     * Get corresponding MarketplaceConfiguration using specified MarketplaceId
     */
    public MarketplaceConfiguration getConfiguration(String marketplaceId,
            HttpServletRequest request) {
        MarketplaceConfiguration conf = configurationCache.get(marketplaceId);
        if (conf == null) {
            conf = new MarketplaceConfiguration();
            VOMarketplace voMarketPlace = null;
            List<VOOrganization> allowedOrgs = null;
            try {
                voMarketPlace = getMarketplaceService(request)
                        .getMarketplaceById(marketplaceId);
                allowedOrgs = getMarketplaceService(request)
                        .getAllOrganizationsWithAccessToMarketplace(
                                marketplaceId);
            } catch (ObjectNotFoundException e) {
                // Should not happen, because already checked by
                // MarketplaceContextFilter
                // But, log error and return default
                getLogger().logError(Log4jLogger.SYSTEM_LOG, e,
                        LogMessageIdentifier.ERROR_MARKETPLACE_NOT_FOUND,
                        marketplaceId);
                return new MarketplaceConfiguration();
            }

            Set<String> idSet = new TreeSet<String>();
            for (VOOrganization org : allowedOrgs) {
                idSet.add(org.getOrganizationId());
            }

            // Copy related attribute from VOMarketplace to
            // MarketplaceConfiguration
            copyAttribute(voMarketPlace, conf);
            conf.setAllowedOrganizations(idSet);
            configurationCache.put(marketplaceId, conf);
        }
        return conf;
    }

    public MarketplaceConfiguration getConfiguration(String marketplaceId) {
        return getConfiguration(marketplaceId, null);
    }

    /**
     * Convenient method for converting from VOMarketplace to
     * MarketplaceConfiguration
     */
    private void copyAttribute(VOMarketplace voMarketPlace,
            MarketplaceConfiguration conf) {
        conf.setReviewEnabled(voMarketPlace.isReviewEnabled());
        conf.setSocialBookmarkEnabled(voMarketPlace.isSocialBookmarkEnabled());
        conf.setTaggingEnabled(voMarketPlace.isTaggingEnabled());
        conf.setCategoriesEnabled(voMarketPlace.isCategoriesEnabled());
        conf.setRestricted(voMarketPlace.isRestricted());
        conf.setLandingPage(voMarketPlace.isHasPublicLandingPage());
    }

    /**
     * Reset configuration with specified Marketplace ID.
     */
    public void resetConfiguration(String marketplaceId) {
        configurationCache.remove(marketplaceId);
    }

    /**
     * Get configuration of the currently used marketplace.
     */
    public MarketplaceConfiguration getCurrentConfiguration() {

        return this.getConfiguration(getMarketplaceId());

    }

    protected String getMarketplaceId() {
        return BaseBean.getMarketplaceIdStatic();
    }

    protected Log4jLogger getLogger() {
        return logger;
    }
}
