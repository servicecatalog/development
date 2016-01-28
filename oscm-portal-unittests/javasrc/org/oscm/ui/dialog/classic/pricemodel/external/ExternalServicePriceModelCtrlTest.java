/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                 
 *                                                                                                                                 
 *  Creation Date: 05.02.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.pricemodel.external;

import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import javax.faces.component.UIViewRoot;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.billing.external.pricemodel.service.PriceModel;
import org.oscm.billing.external.pricemodel.service.PriceModelContent;
import org.oscm.internal.pricemodel.external.ExternalPriceModelException;
import org.oscm.internal.pricemodel.external.ExternalPriceModelService;
import org.oscm.internal.vo.VOServiceDetails;
import org.oscm.ui.beans.ApplicationBean;
import org.oscm.ui.beans.PriceModelBean;
import org.oscm.ui.beans.SessionBean;
import org.oscm.ui.common.UiDelegate;
import org.oscm.ui.stubs.FacesContextStub;

/**
 * @author iversen
 * 
 */
public class ExternalServicePriceModelCtrlTest {

    private ExternalServicePriceModelCtrl ctrl;
    private ExternalPriceModelModel model;
    private PriceModelBean priceModelBean;
    private Locale locale = Locale.ENGLISH;
    private ApplicationBean appbean;
    private SessionBean sessionBean;
    private ExternalPriceModelService externalPriceModelService;
    private static final UUID PRICE_MODEL_UUID1 = UUID
            .fromString("392b159e-e60a-41c4-8495-4fd4b6252ed9");

    @Before
    public void beforeClass() {
        ctrl = spy(new ExternalServicePriceModelCtrl());
        model = spy(new ExternalPriceModelModel());
        priceModelBean = mock(PriceModelBean.class);
        ctrl.setModel(model);
        ctrl.ui = mock(UiDelegate.class);

        appbean = mock(ApplicationBean.class);
        externalPriceModelService = mock(ExternalPriceModelService.class);
        sessionBean = mock(SessionBean.class);
        doReturn(sessionBean).when(ctrl).getSessionBean();

        doReturn(locale).when(appbean).getDefaultLocale();
        doReturn(priceModelBean).when(ctrl).getPriceModelBean();
        doReturn(externalPriceModelService).when(ctrl)
                .getExternalPriceModelService();

        UIViewRoot viewRoot = mock(UIViewRoot.class);
        given(viewRoot.getLocale()).willReturn(locale);
        new FacesContextStub(locale).setViewRoot(viewRoot);
    }

    @Test
    public void testUpload() throws ExternalPriceModelException {

        // given
        PriceModel priceModel = createExternalPriceModel(PRICE_MODEL_UUID1,
                new Locale("en"));
        doReturn(priceModel).when(externalPriceModelService)
                .getExternalPriceModelForService(
                        Mockito.any(VOServiceDetails.class));
        doReturn(true).when(priceModelBean).isExternalServiceSelected();

        // when
        ctrl.upload();

        // then
        verify(externalPriceModelService, times(1))
                .getExternalPriceModelForService(
                        Mockito.any(VOServiceDetails.class));

    }
    
    @Test
    public void testDisplay() throws IOException {

        // given
        PriceModelContent createPriceModelContent = createPriceModelContent();
        doReturn(createPriceModelContent).when(model).getSelectedPriceModelContent();
      
        // when
        String result = ctrl.display();

        // then
        assertNull(result);

    }

    private PriceModel createExternalPriceModel(UUID id, Locale locale) {
        PriceModelContent priceModelContent = createPriceModelContent();
        return createPriceModel(priceModelContent, id, locale);
    }

    private PriceModel createPriceModel(PriceModelContent priceModelContent,
            UUID priceModelUUID, Locale locale) {
        PriceModel priceModel = new PriceModel(priceModelUUID);
        priceModel.put(locale, priceModelContent);
        return priceModel;
    }

    private PriceModelContent createPriceModelContent() {
        String contentType = MediaType.APPLICATION_JSON;
        String priceModelJson = "PRICES:15";
        String priceTag = "15EUR";
        PriceModelContent priceModelContent = new PriceModelContent(contentType,
                priceModelJson.getBytes(), priceTag);
        return priceModelContent;
    }
}
