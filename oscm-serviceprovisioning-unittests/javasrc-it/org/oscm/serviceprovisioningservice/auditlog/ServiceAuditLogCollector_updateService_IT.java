/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 2013-5-23                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.serviceprovisioningservice.auditlog;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import org.oscm.auditlog.AuditLogData;
import org.oscm.auditlog.AuditLogParameter;
import org.oscm.auditlog.BESAuditLogEntry;
import org.oscm.auditlog.model.AuditLogEntry;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Organization;
import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.Product;
import org.oscm.domobjects.enums.LocalizedObjectTypes;
import org.oscm.i18nservice.local.LocalizerServiceLocal;
import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;

/**
 * @author zhaohang
 */

public class ServiceAuditLogCollector_updateService_IT extends EJBTestBase {
    private final static boolean DESCRIPTIONCHANGED = true;
    private final static boolean SHORTDESCRIPTIONCHANGED = true;
    private final static boolean DESCRIPTIONNOTCHANGED = false;
    private final static boolean SHORTDESCRIPTIONNOTCHANGED = false;
    private final static String ORGANIZATIONID = "organizationid";
    private final static String LOCALE = "en";
    private final static String USERID = "userid";
    private final static long SERVICEID = 76234;
    private final static String SERVICENAME = "servicename";

    private static DataService dsMock;
    private static ServiceAuditLogCollector logCollector = new ServiceAuditLogCollector();
    private static LocalizerServiceLocal localizerMock;
    private final static String LOCALIZED_RESOURCE = "TEST";

    private final static String HAVEDESCRIPTION = "YES";
    private final static String HAVENODESCRIPTION = "NO";
    private final static String HAVESHORTDESCRIPTION = "YES";
    private final static String HAVENOSHORTDESCRIPTION = "NO";
    private final static boolean AUTOASSIGNUSERENABLED = true;
    private final static boolean AUTOASSIGNUSERDISABLED = false;

    @Override
    protected void setup(TestContainer container) {
        container.enableInterfaceMocking(true);
        dsMock = mock(DataService.class);
        when(dsMock.getCurrentUser()).thenReturn(givenUser());

        localizerMock = mock(LocalizerServiceLocal.class);
        when(
                localizerMock.getLocalizedTextFromDatabase(Mockito.anyString(),
                        Mockito.anyLong(),
                        Mockito.any(LocalizedObjectTypes.class))).thenReturn(
                                LOCALIZED_RESOURCE);
        logCollector.localizer = localizerMock;
    }

    private PlatformUser givenUser() {
        Organization org = new Organization();
        org.setOrganizationId(ORGANIZATIONID);
        PlatformUser user = new PlatformUser();
        user.setUserId(USERID);
        user.setOrganization(org);
        return user;
    }

    @Test
    public void updateService_DescriptionAndShortDescription() {

        // given
        Product product = givenProduct();

        // when
        updateService(product, DESCRIPTIONCHANGED, SHORTDESCRIPTIONCHANGED);

        // then
        verifyLogEntries(HAVEDESCRIPTION, HAVESHORTDESCRIPTION,
                AUTOASSIGNUSERDISABLED);
    }

    @Test
    public void updateService_DescriptionAndNOShortDescription() {

        // given
        Product product = givenProduct();

        // when
        updateService(product, DESCRIPTIONCHANGED, SHORTDESCRIPTIONNOTCHANGED);

        // then
        verifyLogEntries(HAVEDESCRIPTION, HAVENOSHORTDESCRIPTION,
                AUTOASSIGNUSERDISABLED);
    }

    @Test
    public void updateService_NODescriptionAndShortDescription() {

        // given
        Product product = givenProduct();

        // when
        updateService(product, DESCRIPTIONNOTCHANGED, SHORTDESCRIPTIONCHANGED);

        // then
        verifyLogEntries(HAVENODESCRIPTION, HAVESHORTDESCRIPTION,
                AUTOASSIGNUSERDISABLED);
    }

    @Test
    public void updateService_NODescriptionAndNOShortDescription() {

        // given
        Product product = givenProduct();

        // when
        updateService(product, DESCRIPTIONNOTCHANGED,
                SHORTDESCRIPTIONNOTCHANGED);

        // then
        verifyLogEntries(HAVENODESCRIPTION, HAVENOSHORTDESCRIPTION,
                AUTOASSIGNUSERDISABLED);
    }

    @Test
    public void updateService_AutoAssignUserEnabled() {

        // given
        Product product = givenProduct();
        product.setAutoAssignUserEnabled(Boolean.TRUE);
        // when
        updateService(product, DESCRIPTIONNOTCHANGED,
                SHORTDESCRIPTIONNOTCHANGED);

        // then
        verifyLogEntries(HAVENODESCRIPTION, HAVENOSHORTDESCRIPTION,
                AUTOASSIGNUSERENABLED);
    }

    private ServiceAuditLogCollector updateService(Product product,
            boolean isDescriptionChanged, boolean isShortDescriptionChanged) {
        AuditLogData.clear();
        logCollector.updateService(dsMock, product, isShortDescriptionChanged,
                isDescriptionChanged, LOCALE);
        return logCollector;
    }

    private void verifyLogEntries(String isHaveDescription,
            String isHaveShortDescription, boolean autoAssignUserEnabled) {
        List<AuditLogEntry> logEntries = AuditLogData.get();
        assertEquals(1, logEntries.size());
        BESAuditLogEntry logEntry = (BESAuditLogEntry) AuditLogData.get()
                .get(0);
        Map<AuditLogParameter, String> logParams = logEntry.getLogParameters();
        assertEquals(SERVICENAME, logParams.get(AuditLogParameter.SERVICE_ID));
        assertEquals(LOCALIZED_RESOURCE,
                logParams.get(AuditLogParameter.SERVICE_NAME));
        assertEquals(isHaveShortDescription,
                logParams.get(AuditLogParameter.SHORT_DESCRIPTION));
        assertEquals(isHaveDescription,
                logParams.get(AuditLogParameter.DESCRIPTION));
        assertEquals(LOCALE, logParams.get(AuditLogParameter.LOCALE));
        assertEquals((autoAssignUserEnabled ? "YES" : "NO"),
                logParams.get(AuditLogParameter.AUTO_ASSIGN_USER));
    }

    private Product givenProduct() {
        Product product = new Product();
        product.setKey(SERVICEID);
        product.setProductId(SERVICENAME);
        product.setAutoAssignUserEnabled(Boolean.FALSE);
        return product;
    }

}
