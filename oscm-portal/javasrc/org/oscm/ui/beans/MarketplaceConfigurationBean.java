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
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.oscm.internal.intf.MarketplaceService;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOMarketplace;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.ui.common.JSFUtils;
import org.oscm.ui.common.ServiceAccess;
import org.oscm.ui.model.MarketplaceConfiguration;

/**
 * Session scope bean for caching marketplace configurations.
 * 
 * @author Zou
 * 
 */
@SessionScoped
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
    protected MarketplaceService getMarketplaceService() {
        if (marketplaceService == null) {
            // Get MarketspaceService from Session
            marketplaceService = ServiceAccess.getServiceAcccessFor(
                    JSFUtils.getRequest().getSession()).getService(
                    MarketplaceService.class);
        }
        return marketplaceService;
    }

    /**
     * Get corresponding MarketplaceConfiguration using specified MarketplaceId
     */
    MarketplaceConfiguration getConfiguration(String marketplaceId) {
        MarketplaceConfiguration conf = configurationCache.get(marketplaceId);
        if (conf == null) {
            conf = new MarketplaceConfiguration();
            VOMarketplace voMarketPlace = null;
            try {
                voMarketPlace = getMarketplaceService().getMarketplaceById(
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
            // Copy related attribute from VOMarketplace to
            // MarketplaceConfiguration
            copyAttribute(voMarketPlace, conf);
            configurationCache.put(marketplaceId, conf);
        }
        return conf;
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
