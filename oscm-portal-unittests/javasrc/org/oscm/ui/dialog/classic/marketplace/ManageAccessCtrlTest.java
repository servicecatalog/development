/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: kowalczyka                                                      
 *                                                                              
 *  Creation Date: 18.05.2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.marketplace;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.oscm.internal.intf.MarketplaceService;
import org.oscm.internal.vo.VOMarketplace;

public class ManageAccessCtrlTest {

    private ManageAccessCtrl ctrl;
    private ManageAccessModel model;
    private MarketplaceService marketplaceService;

    private static final String MARKETPLACE_ID = "marketplace1";
    private static final String MARKETPLACE_NAME = "marketplace1Name";

    @Before
    public void setup() {

        marketplaceService = mock(MarketplaceService.class);

        ctrl = new ManageAccessCtrl();
        model = new ManageAccessModel();

        ctrl.setModel(model);
        ctrl.setMarketplaceService(marketplaceService);
    }

    @Test
    public void testInitializedMarketplaces() {

        // given
        doReturn(getSampleMarketplaces()).when(marketplaceService)
                .getMarketplacesOwned();

        // when
        ctrl.initialize();

        // then
        verify(marketplaceService, times(1)).getMarketplacesOwned();
        assertEquals(2, model.getSelectableMarketplaces().size());
    }

    @Test
    public void testSelectedMarketplace() throws Exception {

        // given
        model.setSelectedMarketplaceId(MARKETPLACE_ID);
        doReturn(createSampleMarketplace(MARKETPLACE_NAME, MARKETPLACE_ID))
                .when(marketplaceService).getMarketplaceById(MARKETPLACE_ID);

        // when
        ctrl.marketplaceChanged();

        // then
        verify(marketplaceService, times(1)).getMarketplaceById(MARKETPLACE_ID);
    }
    
    @Test
    public void testNotSelectedMarketplace() throws Exception {

        // given
        model.setSelectedMarketplaceId(null);

        // when
        ctrl.marketplaceChanged();

        // then
        verify(marketplaceService, times(0)).getMarketplaceById(MARKETPLACE_ID);
        assertEquals(false, model.isSelectedMarketplaceRestricted());
    }

    private List<VOMarketplace> getSampleMarketplaces() {

        VOMarketplace marketplace1 = createSampleMarketplace("TestMarketplace1",
                "c34567fg");
        VOMarketplace marketplace2 = createSampleMarketplace("TestMarketplace2",
                "45tf7s20");

        List<VOMarketplace> marketplaces = new ArrayList<>();
        marketplaces.add(marketplace1);
        marketplaces.add(marketplace2);

        return marketplaces;
    }

    private VOMarketplace createSampleMarketplace(String name, String id) {

        VOMarketplace marketplace = new VOMarketplace();
        marketplace.setMarketplaceId(id);
        marketplace.setName(name);

        return marketplace;
    }
}
