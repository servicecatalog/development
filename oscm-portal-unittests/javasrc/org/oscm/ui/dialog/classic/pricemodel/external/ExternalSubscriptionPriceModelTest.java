/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 24 lut 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.pricemodel.external;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Locale;

import javax.faces.component.UIViewRoot;

import org.junit.Before;
import org.junit.Test;
import org.oscm.billing.external.pricemodel.service.PriceModelContent;
import org.oscm.internal.pricemodel.external.ExternalPriceModelService;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.vo.VOPriceModel;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOSubscriptionDetails;
import org.oscm.ui.beans.ApplicationBean;
import org.oscm.ui.beans.PriceModelBean;
import org.oscm.ui.beans.SessionBean;
import org.oscm.ui.common.UiDelegate;
import org.oscm.ui.stubs.FacesContextStub;

/**
 * @author BadziakP
 *
 */
public class ExternalSubscriptionPriceModelTest extends ExternalPriceModelTest {
    private ExternalSubscriptionPriceModelCtrl ctrl;
    private ExternalPriceModelModel model;
    private PriceModelBean priceModelBean;
    private Locale locale = Locale.ENGLISH;
    private ApplicationBean appbean;
    private SessionBean sessionBean;
    private ExternalPriceModelService externalPriceModelService;

    @Before
    public void beforeClass() {
        ctrl = spy(new ExternalSubscriptionPriceModelCtrl());
        model = spy(new ExternalPriceModelModel());
        priceModelBean = mock(PriceModelBean.class);
        ctrl.setModel(model);
        ctrl.ui = mock(UiDelegate.class);

        appbean = mock(ApplicationBean.class);
        externalPriceModelService = mock(ExternalPriceModelService.class);
        sessionBean = mock(SessionBean.class);
        doReturn(sessionBean).when(ctrl).getSessionBean();

        doReturn(locale).when(appbean).getDefaultLocale();
        doReturn(externalPriceModelService).when(ctrl)
                .getExternalPriceModelService();

        UIViewRoot viewRoot = mock(UIViewRoot.class);
        given(viewRoot.getLocale()).willReturn(locale);
        new FacesContextStub(locale).setViewRoot(viewRoot);
    }

    @Test
    public void testUpload() throws SaaSApplicationException {

        // given
        VOPriceModel voPriceModel = new VOPriceModel();
        voPriceModel.setExternal(true);
        VOSubscriptionDetails voSubscriptionDetails = new VOSubscriptionDetails();
        voSubscriptionDetails.setPriceModel(voPriceModel);

        when(priceModelBean.getSelectedSubscription())
                .thenReturn(voSubscriptionDetails);
        when(priceModelBean.validateSubscription(any(VOService.class)))
                .thenReturn(voSubscriptionDetails);

        // when
        ctrl.upload(voSubscriptionDetails);

        // then
        verify(externalPriceModelService, times(1))
                .getExternalPriceModelForSubscription(
                        any(VOSubscriptionDetails.class));
    }

    @Test
    public void testDisplay() throws IOException, SaaSApplicationException {

        // given
        PriceModelContent createPriceModelContent = createPriceModelContent();
        doReturn(createPriceModelContent).when(model)
                .getSelectedPriceModelContent();

        // when
        ctrl.display();

    }
}
