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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.oscm.internal.intf.MarketplaceService;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOMarketplace;
import org.oscm.logging.Log4jLogger;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.ui.model.MarketplaceConfiguration;

/**
 * Test cases for MarketplaceConfigurationBean.
 */
public class MarketplaceConfigurationBeanTest {

    private MarketplaceService msMock;
    private VOMarketplace marketplace;
    private MarketplaceConfigurationBean beanSpy;

    @Before
    public void setup() throws Exception {
        msMock = Mockito.mock(MarketplaceService.class);

        beanSpy = spy(new MarketplaceConfigurationBean());
        doReturn(msMock).when(beanSpy).getMarketplaceService(null);

        marketplace = new VOMarketplace();
        marketplace.setMarketplaceId("dummy");
        marketplace.setReviewEnabled(true);
        marketplace.setSocialBookmarkEnabled(true);
        marketplace.setTaggingEnabled(true);
        marketplace.setCategoriesEnabled(true);
        marketplace.setRestricted(true);
        marketplace.setHasPublicLandingPage(true);
        when(msMock.getMarketplaceById(Matchers.anyString())).thenReturn(
                marketplace);
        doReturn("dummy").when(beanSpy).getMarketplaceId();
    }

    @Test
    public void testGetCurrentConfiguration() {
        MarketplaceConfiguration mpc = beanSpy.getCurrentConfiguration();
        Assert.assertTrue(mpc.isReviewEnabled());
        Assert.assertTrue(mpc.isSocialBookmarkEnabled());
        Assert.assertTrue(mpc.isTaggingEnabled());
        Assert.assertTrue(mpc.isCategoriesEnabled());
        Assert.assertTrue(mpc.isRestricted());
        Assert.assertTrue(mpc.hasLandingPage());
    }

    @Test
    public void testGetConfiguration() {
        MarketplaceConfiguration mpc = beanSpy.getConfiguration("dummy");
        Assert.assertTrue(mpc.isReviewEnabled());
        Assert.assertTrue(mpc.isSocialBookmarkEnabled());
        Assert.assertTrue(mpc.isTaggingEnabled());
        Assert.assertTrue(mpc.isCategoriesEnabled());
        Assert.assertTrue(mpc.isRestricted());
        Assert.assertTrue(mpc.hasLandingPage());
    }

    @Test
    public void testGetConfiguration_disabledAll() {
        marketplace.setMarketplaceId("dummy");
        marketplace.setReviewEnabled(false);
        marketplace.setSocialBookmarkEnabled(false);
        marketplace.setTaggingEnabled(false);
        marketplace.setCategoriesEnabled(false);
        marketplace.setRestricted(false);
        marketplace.setHasPublicLandingPage(false);
        MarketplaceConfiguration mpc = beanSpy.getConfiguration("dummy");
        Assert.assertFalse(mpc.isReviewEnabled());
        Assert.assertFalse(mpc.isSocialBookmarkEnabled());
        Assert.assertFalse(mpc.isTaggingEnabled());
        Assert.assertFalse(mpc.isCategoriesEnabled());
        Assert.assertFalse(mpc.isRestricted());
        Assert.assertFalse(mpc.hasLandingPage());
    }

    @Test
    public void testGetConfiguration_MarketplaceNotFound() throws Exception {
        // Mock logging
        Log4jLogger loggerMock = mock(Log4jLogger.class);
        when(beanSpy.getLogger()).thenReturn(loggerMock);

        final String marketplaceId = "notFound";
        when(msMock.getMarketplaceById(Matchers.anyString())).thenThrow(
                new ObjectNotFoundException());

        // Simulate accessing none existing marketplace
        MarketplaceConfiguration mpc = beanSpy.getConfiguration(marketplaceId);

        // Ensure error is logged...
        verify(loggerMock, times(1)).logError(
                Matchers.eq(Log4jLogger.SYSTEM_LOG),
                Matchers.any(ObjectNotFoundException.class),
                Matchers.eq(LogMessageIdentifier.ERROR_MARKETPLACE_NOT_FOUND),
                Matchers.eq(marketplaceId));

        // and default configuration is returned
        Assert.assertTrue(mpc.isReviewEnabled());
        Assert.assertTrue(mpc.isSocialBookmarkEnabled());
        Assert.assertTrue(mpc.isTaggingEnabled());
    }

    @Test
    public void testResetConfiguration() {
        // Cache it
        MarketplaceConfiguration mpc = beanSpy.getConfiguration("dummy");
        Assert.assertTrue(mpc.isReviewEnabled());
        Assert.assertTrue(mpc.isSocialBookmarkEnabled());
        Assert.assertTrue(mpc.isTaggingEnabled());
        // Remove "dummy" from cache
        beanSpy.resetConfiguration("dummy");
        // Insert a new one with different "ReviewEnabled",
        // "SocialBookmarkEnabled", "TaggingEnabled"
        marketplace.setMarketplaceId("dummy");
        marketplace.setReviewEnabled(false);
        marketplace.setSocialBookmarkEnabled(false);
        marketplace.setTaggingEnabled(false);
        marketplace.setCategoriesEnabled(false);
        marketplace.setRestricted(false);
        marketplace.setHasPublicLandingPage(false);
        // Fetch the newly created one and verify the filed respectively
        mpc = beanSpy.getConfiguration("dummy");
        Assert.assertFalse(mpc.isReviewEnabled());
        Assert.assertFalse(mpc.isSocialBookmarkEnabled());
        Assert.assertFalse(mpc.isTaggingEnabled());
        Assert.assertFalse(mpc.isCategoriesEnabled());
        Assert.assertFalse(mpc.isRestricted());
        Assert.assertFalse(mpc.hasLandingPage());
    }

}
