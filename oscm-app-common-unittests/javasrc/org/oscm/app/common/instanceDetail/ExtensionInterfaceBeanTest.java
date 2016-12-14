/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 2016/11/16                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.common.instanceDetail;

import static org.junit.Assert.assertEquals;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.app.common.intf.InstanceAccess;
import org.oscm.app.common.intf.ServerInformation;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;

/**
 * @author tateiwamext
 * 
 */
public class ExtensionInterfaceBeanTest extends EJBTestBase {

    private FacesContext facesContext;
    private ExternalContext externalContext;
    private Application application;
    private InstanceAccess instanceAccess;
    private List<? extends ServerInformation> serverInfo;
    private String instanceID = "inst1";
    private String subscriptionID = "subscription id";
    private String organizationID = "12345f";
    private Map<String, String> paramters;

    private ExtensionInterfaceBean getTestBean(String instanceId,
            String subscriptionId, String organizationId) throws Exception {
        facesContext = Mockito.mock(FacesContext.class);
        externalContext = Mockito.mock(ExternalContext.class);
        application = Mockito.mock(Application.class);
        instanceAccess = Mockito.mock(InstanceAccess.class);
        UIViewRoot viewRoot = Mockito.mock(UIViewRoot.class);
        paramters = new HashMap<String, String>();
        if (subscriptionId != null) {
            String encodeSubId = URLEncoder.encode(subscriptionId, "UTF-8");
            String decodeSubId = URLDecoder.decode(encodeSubId, "ISO_8859_1");
            paramters.put("subId", decodeSubId);
        } else {
            paramters.put("subId", subscriptionId);
        }
        paramters.put("instId", instanceId);
        paramters.put("orgId", organizationId);

        serverInfo = getServerInfoMock(3);

        Mockito.when(facesContext.getExternalContext())
                .thenReturn(externalContext);
        Mockito.when(facesContext.getApplication()).thenReturn(application);
        Mockito.when(externalContext.getRequestParameterMap())
                .thenReturn(paramters);
        Mockito.when(facesContext.getViewRoot()).thenReturn(viewRoot);
        Mockito.when(viewRoot.getLocale()).thenReturn(new Locale("en"));
        Mockito.when(instanceAccess.getAccessInfo(instanceId, subscriptionId,
                organizationId)).thenReturn("Access info from IaaS");
        Mockito.<List<? extends ServerInformation>> when(instanceAccess
                .getServerDetails(instanceId, subscriptionId, organizationId))
                .thenReturn(serverInfo);

        ExtensionInterfaceBean bean = new ExtensionInterfaceBean() {
            private static final long serialVersionUID = -7419653173313779916L;

            @Override
            protected FacesContext getContext() {
                return facesContext;
            }
        };

        bean.setInstanceAccess(instanceAccess);

        return bean;
    }

    /**
     * @return
     */
    private List<ServerInformation> getServerInfoMock(int numberOfServer) {
        List<ServerInformation> result = new ArrayList<ServerInformation>();
        List<String> status = Arrays.asList("ACTIVE", "STOPPED", "ERROR");
        List<String> type = Arrays.asList("S-1", "P-1", "S-2");
        for (int i = 0; i < numberOfServer; i++) {
            ServerInformation server = Mockito.mock(ServerInformation.class);
            Mockito.when(server.getId()).thenReturn(String.valueOf(i + 1));
            Mockito.when(server.getName())
                    .thenReturn("instance" + String.valueOf(i + 1));
            Mockito.when(server.getPrivateIP())
                    .thenReturn(Arrays.asList("192.168.0." + i,
                            "192.168.0." + String.valueOf(i + 1),
                            "192.168.0." + String.valueOf(i + 2)));
            Mockito.when(server.getPublicIP()).thenReturn(Arrays
                    .asList("10.1.0." + i, "10.1.0." + String.valueOf(i + 1)));
            Mockito.when(server.getStatus())
                    .thenReturn(status.get(3 % (i + 1)));
            Mockito.when(server.getType()).thenReturn(type.get(3 % (i + 1)));
            Mockito.when(server.getPrivateIPasString())
                    .thenReturn("192.168.0." + i + "\n192.168.0."
                            + String.valueOf(i + 1) + "\n192.168.0."
                            + String.valueOf(i + 2));
            Mockito.when(server.getPublicIPasString()).thenReturn(
                    "10.1.0." + i + "\n10.1.0." + String.valueOf(i + 1));
            result.add(server);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.oscm.test.EJBTestBase#setup(org.oscm.test.ejb.TestContainer)
     */
    @Override
    protected void setup(TestContainer container) throws Exception {
        // TODO Auto-generated method stub

    }

    @Test
    public void getInstanceDetails() throws Exception {
        // given
        ExtensionInterfaceBean bean = getTestBean(instanceID, subscriptionID,
                organizationID);
        // when
        List<? extends ServerInformation> sv = bean.getInstanceDetails();
        // then
        assertEquals(3, sv.size());
        assertEquals("1", sv.get(0).getId());
        assertEquals("instance1", sv.get(0).getName());
        assertEquals("192.168.0.0\n192.168.0.1\n192.168.0.2",
                sv.get(0).getPrivateIPasString());
        assertEquals("10.1.0.0\n10.1.0.1", sv.get(0).getPublicIPasString());
        assertEquals("ACTIVE", sv.get(0).getStatus());
        assertEquals("S-1", sv.get(0).getType());

        // call second time
        List<? extends ServerInformation> sv2 = bean.getInstanceDetails();
        // then
        Mockito.verify(instanceAccess, Mockito.times(2))
                .getServerDetails(instanceID, subscriptionID, organizationID);
        assertEquals(3, sv2.size());
        assertEquals("1", sv2.get(0).getId());
        assertEquals("instance1", sv2.get(0).getName());
        assertEquals("192.168.0.0\n192.168.0.1\n192.168.0.2",
                sv.get(0).getPrivateIPasString());
        assertEquals("10.1.0.0\n10.1.0.1", sv2.get(0).getPublicIPasString());
        assertEquals("ACTIVE", sv2.get(0).getStatus());
        assertEquals("S-1", sv2.get(0).getType());

    }

    @Test
    public void getInstanceDetails_withException() throws Exception {
        // given
        ExtensionInterfaceBean bean = getTestBean(instanceID, subscriptionID,
                organizationID);
        Mockito.<List<? extends ServerInformation>> when(instanceAccess
                .getServerDetails(instanceID, subscriptionID, organizationID))
                .thenThrow(new APPlatformException("Error!!"));
        // when
        List<? extends ServerInformation> sv = bean.getInstanceDetails();
        // then
        assertEquals(0, sv.size());
    }

    @Test
    public void getInstanceDetails_withNoInstanceID() throws Exception {
        // given
        ExtensionInterfaceBean bean = getTestBean(null, subscriptionID,
                organizationID);
        Mockito.<List<? extends ServerInformation>> when(instanceAccess
                .getServerDetails(null, subscriptionID, organizationID))
                .thenReturn(getServerInfoMock(0));
        // when
        List<? extends ServerInformation> sv = bean.getInstanceDetails();
        // then
        assertEquals(0, sv.size());
    }

    @Test
    public void getInstanceDetails_withNoSubscriptionID() throws Exception {
        // given
        ExtensionInterfaceBean bean = getTestBean(instanceID, null,
                organizationID);
        Mockito.<List<? extends ServerInformation>> when(instanceAccess
                .getServerDetails(instanceID, null, organizationID))
                .thenReturn(getServerInfoMock(0));
        // when
        List<? extends ServerInformation> sv = bean.getInstanceDetails();
        // then
        assertEquals(0, sv.size());
    }

    @Test
    public void getInstanceDetails_withNoOrganizationID() throws Exception {
        // given
        ExtensionInterfaceBean bean = getTestBean(instanceID, subscriptionID,
                null);
        Mockito.<List<? extends ServerInformation>> when(instanceAccess
                .getServerDetails(instanceID, subscriptionID, null))
                .thenReturn(getServerInfoMock(0));
        // when
        List<? extends ServerInformation> sv = bean.getInstanceDetails();
        // then
        assertEquals(0, sv.size());
    }

    @Test
    public void getSubscriptionName() throws Exception {
        // given
        ExtensionInterfaceBean bean = getTestBean(instanceID, subscriptionID,
                organizationID);

        // when
        String result = bean.getSubscriptionName();

        // then
        assertEquals("subscription id", result);

    }

    @Test
    public void getSubscriptionName_nothing() throws Exception {
        // given
        ExtensionInterfaceBean bean = getTestBean(instanceID, null,
                organizationID);

        // when
        String result = bean.getSubscriptionName();

        // then
        assertEquals("", result);

    }

    @Test
    public void getSubscriptionName_withJapanese() throws Exception {
        // given
        String japaneseSubId = "abcd あいう 123";
        ExtensionInterfaceBean bean = getTestBean(instanceID, japaneseSubId,
                organizationID);

        // when
        String result = bean.getSubscriptionName();

        // then
        assertEquals(japaneseSubId, result);

    }

    @Test
    public void getAccessInfo() throws Exception {
        // given
        ExtensionInterfaceBean bean = getTestBean(instanceID, subscriptionID,
                organizationID);

        // when
        String result = bean.getAccessInfo();

        // then
        assertEquals("Access info from IaaS", result);

        // call second time
        String result2 = bean.getAccessInfo();

        // then
        Mockito.verify(instanceAccess, Mockito.times(2))
                .getAccessInfo(instanceID, subscriptionID, organizationID);
        assertEquals("Access info from IaaS", result2);
    }

    @Test
    public void getAccessInfo_withError() throws Exception {
        // given
        ExtensionInterfaceBean bean = getTestBean(instanceID, subscriptionID,
                organizationID);
        Mockito.when(instanceAccess.getAccessInfo(instanceID, subscriptionID,
                organizationID)).thenThrow(new APPlatformException("Error!!!"));

        // when
        String result = bean.getAccessInfo();

        // then
        assertEquals("", result);

    }
}
