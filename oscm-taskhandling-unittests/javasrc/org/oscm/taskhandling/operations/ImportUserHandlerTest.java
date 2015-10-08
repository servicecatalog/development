/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2015 
 *******************************************************************************/

package org.oscm.taskhandling.operations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import org.oscm.communicationservice.local.CommunicationServiceLocal;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.PlatformUser;
import org.oscm.i18nservice.local.LocalizerServiceLocal;
import org.oscm.identityservice.local.IdentityServiceLocal;
import org.oscm.taskhandling.facade.ServiceFacade;
import org.oscm.taskhandling.payloads.ImportUserPayload;
import org.oscm.types.enumtypes.EmailType;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.types.exception.DomainObjectException.ClassEnum;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.vo.VOUserDetails;

/**
 * 
 * Test cases for the class ImportUserHandler
 * 
 * @author cheld
 * 
 */
@SuppressWarnings({ "boxing" })
public class ImportUserHandlerTest {

    CommunicationServiceLocal cs;
    IdentityServiceLocal is;
    LocalizerServiceLocal ls;
    DataService ds;

    PlatformUser importingUser;
    ImportUserHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new ImportUserHandler();
        handler.setServiceFacade(createMocks());
        handler.setPayload(new ImportUserPayload());

        // define importing user
        importingUser = new PlatformUser();
        importingUser.setUserId("importingUser");
        importingUser.setLocale("en");
        importingUser.setKey(1);
        when(ds.getReference(PlatformUser.class, importingUser.getKey()))
                .thenReturn(importingUser);
    }

    private ServiceFacade createMocks() throws Exception {
        ServiceFacade facade = new ServiceFacade();
        cs = mock(CommunicationServiceLocal.class);
        facade.setCommunicationService(cs);
        is = mock(IdentityServiceLocal.class);
        facade.setIdentityService(is);
        ls = mock(LocalizerServiceLocal.class);
        facade.setLocalizerService(ls);
        ds = mock(DataService.class);
        facade.setDataService(ds);
        return facade;
    }

    /**
     * Import one user. The report sent by mail must contain correct numbers.
     */
    @Test
    public void execute_oneUser() throws Exception {

        // given one user to be imported
        given("user1", UserRoleType.ORGANIZATION_ADMIN);

        // when
        handler.execute();

        // then
        Report report = handler.report;
        assertEquals(1, report.allUsersToBeImported());
        assertEquals(1, report.importedUsers());
        assertEquals(0, report.failedUsers());
        assertEquals(EmailType.BULK_USER_IMPORT_SUCCESS, report.buildMailType());
    }

    /**
     * One create user operation fails. Report must show failure
     */
    @Test
    public void execute_failedImport() throws Exception {

        // given server call with throw exception
        given("user1", UserRoleType.ORGANIZATION_ADMIN);
        doThrow(new NonUniqueBusinessKeyException()).when(is).importUser(
                any(VOUserDetails.class), anyString());

        // when
        handler.execute();

        // then report contains errors
        Report report = handler.report;
        assertEquals(1, report.allUsersToBeImported());
        assertEquals(0, report.importedUsers());
        assertEquals(1, report.failedUsers());
        assertEquals(EmailType.BULK_USER_IMPORT_SOME_ERRORS,
                report.buildMailType());
    }

    /**
     * Handle null pointer exceptions gracefully
     */
    @Test
    public void execute_nullPointerException() throws Exception {

        // given server call with throw exception
        given("user1", UserRoleType.ORGANIZATION_ADMIN);
        doThrow(new NullPointerException()).when(is).importUser(
                any(VOUserDetails.class), anyString());

        // when
        handler.execute();

        // then report contains errors
        Report report = handler.report;
        assertEquals(1, report.allUsersToBeImported());
        assertEquals(0, report.importedUsers());
        assertEquals(1, report.failedUsers());
        assertEquals(EmailType.BULK_USER_IMPORT_SOME_ERRORS,
                report.buildMailType());
    }

    /**
     * Marketplace Id might not be set. 
     */
    @Test
    public void execute_noMarketplace() throws Exception {

        // given import without marketplace
        given("user1", UserRoleType.ORGANIZATION_ADMIN);
        handler.payload.setMarketplaceId(null);

        // when
        handler.execute();

        // then no exception
        Report report = handler.report;
        assertEquals(EmailType.BULK_USER_IMPORT_SUCCESS, report.buildMailType());
    }

    private void given(String userId, UserRoleType role) {
        handler.payload.setImportingUserKey(importingUser.getKey());
        handler.payload.setMarketplaceId("marketplaceId");
        VOUserDetails userDetails = new VOUserDetails();
        userDetails.setUserId(userId);
        handler.payload.addUser(userDetails, Collections.singletonList(role));
    }

    /**
     * Replace parameter in text properties
     */
    @Test
    public void formatMessage() {
        String message = handler.formatMessage(
                "The user ID {0} already exists. Please choose another ID.",
                new NonUniqueBusinessKeyException(ClassEnum.USER, "cheld"),
                "en");
        assertEquals(
                "The user ID cheld already exists. Please choose another ID.",
                message);
    }

    /**
     * Use key as fall back text if message is missing in property file for one locale
     */
    @Test
    public void formatMessage_noLocalization() {
        String message = handler.formatMessage("",
                new NonUniqueBusinessKeyException(ClassEnum.USER, "cheld"),
                Locale.KOREA.getLanguage());
        assertEquals("ex.NonUniqueBusinessKeyException.USER", message);
    }

}
