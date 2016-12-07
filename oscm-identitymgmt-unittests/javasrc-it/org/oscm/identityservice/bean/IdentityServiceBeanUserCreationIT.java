/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: Mike J&auml;ger                                                      
 *                                                                              
 *  Creation Date: 08.12.2010                                                      
 *                                                                              
 *  Completion Time:  09.12.2010                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.identityservice.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.ejb.EJBException;

import org.junit.Test;
import org.oscm.configurationservice.local.ConfigurationServiceLocal;
import org.oscm.dataservice.bean.DataServiceBean;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.ConfigurationSetting;
import org.oscm.domobjects.DomainObject;
import org.oscm.domobjects.OnBehalfUserReference;
import org.oscm.domobjects.Organization;
import org.oscm.domobjects.OrganizationReference;
import org.oscm.domobjects.OrganizationRole;
import org.oscm.domobjects.OrganizationToRole;
import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.PlatformUserHistory;
import org.oscm.domobjects.RoleAssignment;
import org.oscm.domobjects.UserGroup;
import org.oscm.domobjects.enums.ModificationType;
import org.oscm.domobjects.enums.OrganizationReferenceType;
import org.oscm.encrypter.AESEncrypter;
import org.oscm.identityservice.local.IdentityServiceLocal;
import org.oscm.interceptor.DateFactory;
import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.UserAccountStatus;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.types.exception.MailOperationException;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.types.exception.OperationPendingException;
import org.oscm.internal.types.exception.UserRoleAssignmentException;
import org.oscm.internal.types.exception.ValidationException;
import org.oscm.internal.vo.VOUser;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.reviewservice.bean.ReviewServiceLocalBean;
import org.oscm.subscriptionservice.local.SubscriptionServiceLocal;
import org.oscm.test.EJBTestBase;
import org.oscm.test.data.Organizations;
import org.oscm.test.ejb.TestContainer;
import org.oscm.test.stubs.ApplicationServiceStub;
import org.oscm.test.stubs.CommunicationServiceStub;
import org.oscm.test.stubs.ConfigurationServiceStub;
import org.oscm.test.stubs.LdapAccessServiceStub;
import org.oscm.test.stubs.SessionServiceStub;
import org.oscm.test.stubs.TaskQueueServiceStub;
import org.oscm.test.stubs.TriggerQueueServiceStub;
import org.oscm.types.constants.Configuration;
import org.oscm.usergroupservice.bean.UserGroupServiceLocalBean;
import org.oscm.usergroupservice.dao.UserGroupDao;
import org.oscm.usergroupservice.dao.UserGroupUsersDao;

/**
 * Test class for the functionality of creating a new user for a specified
 * customer which is available only in supplier context.
 * 
 * @author Mike J&auml;ger
 * 
 */
public class IdentityServiceBeanUserCreationIT extends EJBTestBase {

    private IdentityService idService;
    private DataService dm;
    private ConfigurationServiceLocal cs;
    private Organization supplier;
    private Organization technologyProvider;
    private Organization tpAndSup;
    private PlatformUser supplierAdminUser;
    private Organization supplier2;
    private Organization customer;
    private Organization customer2;
    private int numberOfNUBKEs = 0;
    private IdentityServiceLocal idServiceLocal;
    private PlatformUser customerUser;

    private long period;

    @Override
    protected void setup(TestContainer container) throws Exception {
        AESEncrypter.generateKey();
        container.login("1");
        container.enableInterfaceMocking(true);
        container.addBean(new ConfigurationServiceStub() {
            @Override
            public boolean isServiceProvider() {
                ConfigurationSetting setting = getConfigurationSetting(
                        ConfigurationKey.AUTH_MODE,
                        Configuration.GLOBAL_CONTEXT);
                if ("SAML_SP".equals(setting.getValue())) {
                    return true;

                }
                return false;
            }
        });
        container.addBean(new DataServiceBean() {
            @Override
            public void persist(DomainObject<?> obj)
                    throws NonUniqueBusinessKeyException {
                if (numberOfNUBKEs > 0) {
                    numberOfNUBKEs--;
                    throw new NonUniqueBusinessKeyException();
                }
                super.persist(obj);
            }
        });
        container.addBean(mock(ReviewServiceLocalBean.class));
        container.addBean(new UserGroupDao());
        container.addBean(new UserGroupUsersDao());
        container.addBean(new UserGroupServiceLocalBean());
        container.addBean(new LdapAccessServiceStub());
        container.addBean(new SessionServiceStub());
        container.addBean(new TriggerQueueServiceStub());

        container.addBean(new CommunicationServiceStub() {
            @Override
            public String getMarketplaceUrl(String marketplaceId)
                    throws MailOperationException {
                return "";
            }
        });
        container.addBean(new ApplicationServiceStub());
        container.addBean(mock(SubscriptionServiceLocal.class));
        container.addBean(new TaskQueueServiceStub());
        container.addBean(new IdentityServiceBean());

        dm = container.get(DataService.class);
        idService = container.get(IdentityService.class);
        idServiceLocal = container.get(IdentityServiceLocal.class);

        initData();
        container.login(supplierAdminUser.getKey(), ROLE_SERVICE_MANAGER);
    }

    @Test
    public void testCreateUser_CheckOrganizationVersion() throws Exception {
        container.login(supplierAdminUser.getKey(), ROLE_ORGANIZATION_ADMIN);

        // given
        int oldVersion = supplier.getVersion();
        VOUserDetails user = new VOUserDetails();
        user.setUserId("user_" + System.currentTimeMillis());
        user.setOrganizationId(supplier.getOrganizationId());
        user.setEMail("bes.testuser1@dev.est.fujitsu.com");
        user.setLocale("en");
        idService.createUser(user,
                Collections.singletonList(UserRoleType.SERVICE_MANAGER), null);

        // assert
        Integer result = runTX(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return Integer
                        .valueOf(dm.find(Organization.class, supplier.getKey())
                                .getVersion());
            }
        });
        assertEquals(
                "The organization should not be changed when a user is created!",
                oldVersion, result.intValue());
    }

    private void setRoles(final List<OrganizationRoleType> roleTypes)
            throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Organization org = dm.getReference(Organization.class,
                        supplierAdminUser.getOrganization().getKey());
                dm.createNativeQuery(
                        "delete from organizationtorole where organization_tkey="
                                + supplierAdminUser.getOrganization().getKey())
                        .executeUpdate();
                final Set<OrganizationToRole> roles = new HashSet<>();
                for (OrganizationRoleType roleType : roleTypes) {
                    final OrganizationToRole r1 = new OrganizationToRole();
                    r1.setOrganization(org);
                    OrganizationRole organizationRole = new OrganizationRole(
                            roleType);
                    organizationRole = (OrganizationRole) dm
                            .getReferenceByBusinessKey(organizationRole);
                    r1.setOrganizationRole(organizationRole);
                    dm.persist(r1);
                    roles.add(r1);
                }
                if (roles.size() > 0) {
                    org.setGrantedRoles(roles);
                }
                return null;
            }
        });
    }

    private boolean testRole(UserRoleType role, boolean allowed)
            throws NonUniqueBusinessKeyException, MailOperationException,
            ValidationException, UserRoleAssignmentException,
            OperationPendingException {
        boolean success = false;
        VOUserDetails user = new VOUserDetails();
        user.setUserId("user_" + System.currentTimeMillis());
        user.setOrganizationId(supplier.getOrganizationId());
        user.setEMail("bes.testuser1@dev.est.fujitsu.com");
        user.setLocale("en");
        List<UserRoleType> l = Collections.emptyList();
        if (role != null) {
            l = Collections.singletonList(role);
        }
        if (allowed) {
            idService.createUser(user, l, null);
            success = true;
        } else {
            try {
                idService.createUser(user, l, null);
                success = false;
            } catch (ValidationException ex) {
                success = true;
            }
        }
        return success;
    }

    private void createUserOrgRoles(List<OrganizationRoleType> orgRoles,
            List<UserRoleType> allowedUserRoles) throws Exception {
        container.login(supplierAdminUser.getKey(), ROLE_ORGANIZATION_ADMIN,
                ROLE_PLATFORM_OPERATOR, ROLE_TECHNOLOGY_MANAGER,
                ROLE_MARKETPLACE_OWNER);
        setRoles(orgRoles);
        assertTrue(testRole(null, false));
        assertTrue(testRole(UserRoleType.ORGANIZATION_ADMIN,
                allowedUserRoles.contains(UserRoleType.ORGANIZATION_ADMIN)));
        if (orgRoles.contains(OrganizationRoleType.MARKETPLACE_OWNER)) {
            assertTrue(testRole(UserRoleType.MARKETPLACE_OWNER,
                    allowedUserRoles.contains(UserRoleType.MARKETPLACE_OWNER)));
        }
        if (orgRoles.contains(OrganizationRoleType.TECHNOLOGY_PROVIDER)) {
            assertTrue(
                    testRole(UserRoleType.TECHNOLOGY_MANAGER, allowedUserRoles
                            .contains(UserRoleType.TECHNOLOGY_MANAGER)));
        }
        if (orgRoles.contains(OrganizationRoleType.PLATFORM_OPERATOR)) {
            assertTrue(testRole(UserRoleType.PLATFORM_OPERATOR,
                    allowedUserRoles.contains(UserRoleType.PLATFORM_OPERATOR)));
        }
        if (orgRoles.contains(OrganizationRoleType.SUPPLIER)) {
            assertTrue(testRole(UserRoleType.SERVICE_MANAGER,
                    allowedUserRoles.contains(UserRoleType.SERVICE_MANAGER)));
        }
    }

    @Test
    public void testCreateUser_OrgRolesAll() throws Exception {
        createUserOrgRoles(
                Arrays.asList(OrganizationRoleType.PLATFORM_OPERATOR,
                        OrganizationRoleType.SUPPLIER,
                        OrganizationRoleType.TECHNOLOGY_PROVIDER,
                        OrganizationRoleType.MARKETPLACE_OWNER),
                Arrays.asList(UserRoleType.PLATFORM_OPERATOR,
                        UserRoleType.SERVICE_MANAGER,
                        UserRoleType.TECHNOLOGY_MANAGER,
                        UserRoleType.MARKETPLACE_OWNER,
                        UserRoleType.ORGANIZATION_ADMIN));
    }

    @Test(expected = NonUniqueBusinessKeyException.class)
    public void testCreateUser_ExistingUserId() throws Exception {
        container.login(supplierAdminUser.getKey(), ROLE_ORGANIZATION_ADMIN,
                ROLE_PLATFORM_OPERATOR, ROLE_TECHNOLOGY_MANAGER,
                ROLE_MARKETPLACE_OWNER);
        VOUserDetails user = new VOUserDetails();
        user.setUserId("user_" + System.currentTimeMillis());
        user.setOrganizationId(supplier.getOrganizationId());
        user.setEMail("bes.testuser1@dev.est.fujitsu.com");
        user.setLocale("en");
        List<UserRoleType> l = Arrays.asList(UserRoleType.SERVICE_MANAGER);
        idService.createUser(user, l, null);
        idService.createUser(user, l, null);
    }

    @Test
    public void testCreateUser_OrgRolesPlatformOperator() throws Exception {
        createUserOrgRoles(
                Arrays.asList(OrganizationRoleType.PLATFORM_OPERATOR),
                Arrays.asList(UserRoleType.PLATFORM_OPERATOR,
                        UserRoleType.ORGANIZATION_ADMIN));
    }

    @Test
    public void testCreateUser_OrgRolesSupplier() throws Exception {
        createUserOrgRoles(Arrays.asList(OrganizationRoleType.SUPPLIER),
                Arrays.asList(UserRoleType.SERVICE_MANAGER,
                        UserRoleType.ORGANIZATION_ADMIN));
    }

    @Test
    public void testCreateUser_OrgRolesTechnologyProvider() throws Exception {
        createUserOrgRoles(
                Arrays.asList(OrganizationRoleType.TECHNOLOGY_PROVIDER),
                Arrays.asList(UserRoleType.TECHNOLOGY_MANAGER,
                        UserRoleType.ORGANIZATION_ADMIN));
    }

    @Test
    public void testCreateUser_OrgRolesCustomer() throws Exception {
        createUserOrgRoles(Arrays.asList(OrganizationRoleType.CUSTOMER),
                new ArrayList<UserRoleType>());
    }

    @Test
    public void testCreateUser_OrgRolesMarketplaceOwner() throws Exception {
        createUserOrgRoles(
                Arrays.asList(OrganizationRoleType.MARKETPLACE_OWNER),
                Arrays.asList(UserRoleType.MARKETPLACE_OWNER,
                        UserRoleType.ORGANIZATION_ADMIN));
    }

    @Test
    public void testCreateUser_OrgRolesSupplierAndTechnologyProvider()
            throws Exception {
        createUserOrgRoles(
                Arrays.asList(OrganizationRoleType.SUPPLIER,
                        OrganizationRoleType.TECHNOLOGY_PROVIDER),
                Arrays.asList(UserRoleType.SERVICE_MANAGER,
                        UserRoleType.TECHNOLOGY_MANAGER,
                        UserRoleType.ORGANIZATION_ADMIN));
    }

    @Test(expected = EJBException.class)
    public void testCreateOnBehalfUser_NoValidUser() throws Exception {
        container.login(-1);
        idService.createOnBehalfUser(null, null);
    }

    /**
     * Roles required: SERVICE_MANAGER or TECHNOLOGY_MANAGER
     */
    @Test(expected = EJBException.class)
    public void testCreateOnBehalfUser_notAuthorized() throws Exception {
        container.login(customerUser.getKey(), ROLE_PLATFORM_OPERATOR);
        idService.createOnBehalfUser(null, null);
    }

    @Test(expected = EJBException.class)
    public void testCreateOnBehalfUser_NoCustomerSpecified() throws Exception {
        idService.createOnBehalfUser(null, null);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testCreateOnBehalfUser_NonExistingCustomer() throws Exception {
        idService.createOnBehalfUser("nonexistingid", "");
    }

    /**
     * Supplier is acting on behalf of customer1. For customer2 the reference is
     * missing.
     */
    @Test(expected = OperationNotPermittedException.class)
    public void testCreateOnBehalfUser_notActingOn() throws Exception {
        idService.createOnBehalfUser(customer2.getOrganizationId(), "");
    }

    @Test(expected = EJBException.class)
    public void testCreateOnBehalfUser_NullPassword() throws Exception {
        idService.createOnBehalfUser(customer.getOrganizationId(), null);
    }

    /**
     * Create on behalf user as service manager
     * 
     * @throws Exception
     */
    @Test
    public void testCreateOnBehalfUser() throws Exception {
        String password = "abcdef";
        VOUserDetails result = idService
                .createOnBehalfUser(customer.getOrganizationId(), password);
        checkCreatedUser(result, customer, password, false);
    }

    /**
     * Create on behalf user as technology manager
     * 
     * @throws Exception
     */
    @Test
    public void testCreateOnBehalfUser_asTechnologyManager() throws Exception {
        container.login(supplierAdminUser.getKey(), ROLE_TECHNOLOGY_MANAGER);
        VOUserDetails result = idService
                .createOnBehalfUser(customer.getOrganizationId(), "abcdef");
        checkCreatedUser(result, customer, "abcdef", false);
        checkUserRoleAssignment(result, ROLE_ORGANIZATION_ADMIN);
    }

    @Test
    public void testCreateOnBehalfUser_ForSupplierOrg() throws Exception {
        container.login(supplierAdminUser.getKey(), ROLE_TECHNOLOGY_MANAGER);
        VOUserDetails result = idService
                .createOnBehalfUser(supplier2.getOrganizationId(), "abcdef");
        checkCreatedUser(result, supplier2, "abcdef", false);
        checkUserRoleAssignment(result, ROLE_ORGANIZATION_ADMIN,
                ROLE_SERVICE_MANAGER);
    }

    @Test
    public void testCreateOnBehalfUser_ForSupplierOrg_SAML_SP()
            throws Exception {

        // given
        cs = container.get(ConfigurationServiceLocal.class);
        cs.setConfigurationSetting(
                new ConfigurationSetting(ConfigurationKey.AUTH_MODE,
                        Configuration.GLOBAL_CONTEXT, "SAML_SP"));

        // when
        container.login(supplierAdminUser.getKey(), ROLE_TECHNOLOGY_MANAGER);
        VOUserDetails result = idService
                .createOnBehalfUser(supplier2.getOrganizationId(), "abcdef");

        // then
        checkCreatedUser(result, supplier2, "abcdef", true);
        checkUserRoleAssignment(result, ROLE_ORGANIZATION_ADMIN,
                ROLE_SERVICE_MANAGER);
    }

    @Test
    public void testCreateOnBehalfUserForSupplierOrgSAMLBug11001()
            throws Exception {

        // given
        cs = container.get(ConfigurationServiceLocal.class);
        cs.setConfigurationSetting(
                new ConfigurationSetting(ConfigurationKey.AUTH_MODE,
                        Configuration.GLOBAL_CONTEXT, "SAML_SP"));

        // when
        container.login(supplierAdminUser.getKey(), ROLE_TECHNOLOGY_MANAGER);
        VOUserDetails result = idService
                .createOnBehalfUser(supplier2.getOrganizationId(), "abcdef");
        result = idService.createOnBehalfUser(supplier2.getOrganizationId(),
                "abcdef");

        // then
        checkCreatedUser(result, supplier2, "abcdef", true);
        checkUserRoleAssignment(result, ROLE_ORGANIZATION_ADMIN,
                ROLE_SERVICE_MANAGER);
    }

    @Test
    public void testCreateOnBehalfUser_ForTechnicalProviderOrg()
            throws Exception {
        container.login(supplierAdminUser.getKey(), ROLE_TECHNOLOGY_MANAGER);
        VOUserDetails result = idService.createOnBehalfUser(
                technologyProvider.getOrganizationId(), "abcdef");
        checkCreatedUser(result, technologyProvider, "abcdef", false);
        checkUserRoleAssignment(result, ROLE_ORGANIZATION_ADMIN,
                ROLE_TECHNOLOGY_MANAGER);
    }

    @Test
    public void testCreateOnBehalfUser_ForTPAndSup() throws Exception {
        container.login(supplierAdminUser.getKey(), ROLE_TECHNOLOGY_MANAGER);
        VOUserDetails result = idService
                .createOnBehalfUser(tpAndSup.getOrganizationId(), "abcdef");
        checkCreatedUser(result, tpAndSup, "abcdef", false);
        checkUserRoleAssignment(result, ROLE_ORGANIZATION_ADMIN,
                ROLE_TECHNOLOGY_MANAGER, ROLE_SERVICE_MANAGER);
    }

    /**
     * Validates that the user has the specified roles.
     * 
     * @param consideredUser
     *            The user to set the roles for.
     * @param roleNames
     *            The names of the roles.
     * @throws Exception
     */
    private void checkUserRoleAssignment(final VOUserDetails consideredUser,
            final String... roleNames) throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                PlatformUser user = dm.getReference(PlatformUser.class,
                        consideredUser.getKey());
                Set<RoleAssignment> assignedRoles = user.getAssignedRoles();
                assertEquals(roleNames.length, assignedRoles.size());
                for (String roleName : roleNames) {
                    boolean roleFound = false;
                    for (RoleAssignment roleAssignment : assignedRoles) {
                        if (roleAssignment.getRole().getRoleName().name()
                                .equals(roleName)) {
                            roleFound = true;
                        }
                    }
                    assertTrue("User is missing the required role " + roleName,
                            roleFound);
                }
                return null;
            }
        });
    }

    @Test
    public void testCreateOnBehalfUser_IdClashAcceptable() throws Exception {
        numberOfNUBKEs = 10;
        String password = "abcdef";
        VOUserDetails result = idService
                .createOnBehalfUser(customer.getOrganizationId(), password);
        checkCreatedUser(result, customer, password, false);
    }

    @Test(expected = NonUniqueBusinessKeyException.class)
    public void testCreateOnBehalfUser_IdClashTooMany() throws Exception {
        numberOfNUBKEs = 11;
        idService.createOnBehalfUser(customer.getOrganizationId(), "abcdef");
    }

    @Test
    public void testCleanupCurrentUser_OnBehalfUser() throws Exception {
        VOUserDetails obUser = idService
                .createOnBehalfUser(customer.getOrganizationId(), "pwd123");

        container.login(obUser.getKey());
        PlatformUser newUser = new PlatformUser();
        newUser.setKey(obUser.getKey());
        newUser = getDomainObject(newUser, PlatformUser.class);

        // verify user is deleted
        idService.cleanUpCurrentUser();
        try {
            getDomainObject(newUser, PlatformUser.class);
            fail("User must have been deleted");
        } catch (ObjectNotFoundException e) {
            // expected
        }
        List<PlatformUserHistory> userHist = getHistory(newUser,
                PlatformUserHistory.class);
        assertEquals(ModificationType.DELETE,
                userHist.get(userHist.size() - 1).getModtype());

        // verify user relation is deleted
        OnBehalfUserReference onBehalf = newUser.getMaster();
        try {
            getDomainObject(onBehalf, OnBehalfUserReference.class);
            fail("User relation must have been deleted");
        } catch (ObjectNotFoundException e) {
            // expected
        }

        getDomainObject(supplierAdminUser, PlatformUser.class);

    }

    /**
     * Call clean up of temporary on-behalf-users with a normal user. This user
     * must still exist.
     */
    @Test
    public void testCleanupCurrentUser_NonOnBehalfUser() throws Exception {
        idService.cleanUpCurrentUser();
        // user must still exist
        PlatformUser user = getDomainObject(supplierAdminUser,
                PlatformUser.class);
        assertNotNull(user);
    }

    @Test
    public void testRemoveInactiveOnBehalfUsers_NoUsersPresent()
            throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                idServiceLocal.removeInactiveOnBehalfUsers();
                return null;
            }
        });
        assertTrue(getAllPersistedObjectsOfType(OnBehalfUserReference.class)
                .isEmpty());
    }

    @Test
    public void testRemoveInactiveOnBehalfUsers_TwoUsersPresent()
            throws Exception {
        period = 0;

        cs = container.get(ConfigurationServiceLocal.class);
        cs.setConfigurationSetting(new ConfigurationSetting(
                ConfigurationKey.PERMITTED_PERIOD_INACTIVE_ON_BEHALF_USERS,
                Configuration.GLOBAL_CONTEXT, String.valueOf(period)));
        VOUserDetails custUser1 = idService
                .createOnBehalfUser(customer.getOrganizationId(), "user1");
        PlatformUser custPU1 = new PlatformUser();
        custPU1.setKey(custUser1.getKey());
        custPU1 = getDomainObject(custPU1, PlatformUser.class);
        VOUserDetails custUser2 = idService
                .createOnBehalfUser(customer.getOrganizationId(), "user2");
        PlatformUser custPU2 = new PlatformUser();
        custPU2.setKey(custUser2.getKey());
        custPU2 = getDomainObject(custPU2, PlatformUser.class);
        Long lastAccessTime = Long.valueOf(System.currentTimeMillis() - 1000);
        setSlaveUserAccessTime(custUser1, lastAccessTime);
        setSlaveUserAccessTime(custUser2, lastAccessTime);

        List<OnBehalfUserReference> utobus = getAllPersistedObjectsOfType(
                OnBehalfUserReference.class);
        assertEquals(2, utobus.size());

        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                idServiceLocal.removeInactiveOnBehalfUsers();
                return null;
            }
        });

        // assert deletion of users
        assertTrue(getAllPersistedObjectsOfType(OnBehalfUserReference.class)
                .isEmpty());
        List<PlatformUser> allUsers = getAllPersistedObjectsOfType(
                PlatformUser.class);
        assertFalse(allUsers.contains(custPU1));
        assertFalse(allUsers.contains(custPU2));

        // assert History changes
        for (OnBehalfUserReference currentUserRelation : utobus) {
            // relations must be deleted

            // slave users must be deleted
            List<PlatformUserHistory> slaveHist = getHistory(
                    currentUserRelation.getSlaveUser(),
                    PlatformUserHistory.class);
            assertEquals(ModificationType.DELETE,
                    slaveHist.get(slaveHist.size() - 1).getModtype());
            // master users must remain unchanged
            List<PlatformUserHistory> masterHist = getHistory(
                    currentUserRelation.getMasterUser(),
                    PlatformUserHistory.class);
            assertEquals(ModificationType.ADD,
                    masterHist.get(masterHist.size() - 1).getModtype());
        }
    }

    @Test
    public void testRemoveInactiveOnBehalfUsers_TwoUsersOneToDelete()
            throws Exception {
        period = 500000;

        cs = container.get(ConfigurationServiceLocal.class);
        cs.setConfigurationSetting(new ConfigurationSetting(
                ConfigurationKey.PERMITTED_PERIOD_INACTIVE_ON_BEHALF_USERS,
                Configuration.GLOBAL_CONTEXT, String.valueOf(period)));
        final long permittedPeriod = period;
        final VOUserDetails custUser1 = idService
                .createOnBehalfUser(customer.getOrganizationId(), "user1");
        final VOUserDetails custUser2 = idService
                .createOnBehalfUser(customer.getOrganizationId(), "user2");
        setSlaveUserAccessTime(custUser1,
                Long.valueOf(System.currentTimeMillis() - permittedPeriod - 1));
        setSlaveUserAccessTime(custUser2,
                Long.valueOf(System.currentTimeMillis() - 1));
        long millis = DateFactory.getInstance().getTransactionTime();

        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                idServiceLocal.removeInactiveOnBehalfUsers();
                return null;
            }
        });
        assertEquals(Boolean.FALSE, Boolean.valueOf(
                millis == DateFactory.getInstance().getTransactionTime()));
        List<OnBehalfUserReference> onBehalfUsers = getAllPersistedObjectsOfType(
                OnBehalfUserReference.class);
        assertEquals(1, onBehalfUsers.size());
        assertEquals(custUser2.getKey(),
                onBehalfUsers.get(0).getSlaveUser().getKey());
    }

    /**
     * The list of users for a login organization must not include users created
     * on behalf of another user. Assert that the list size does not change when
     * creating on behalf users.
     * 
     * @throws Exception
     */
    @Test
    public void testGetUsersForOrganization_SubMgr() throws Exception {

        // given
        container.login(customerUser.getKey(), ROLE_ORGANIZATION_ADMIN);
        List<VOUserDetails> oldList = idService.getUsersForOrganization();

        // execute
        container.login(supplierAdminUser.getKey(), ROLE_SUBSCRIPTION_MANAGER);
        List<VOUserDetails> newList = idService.getUsersForOrganization();

        assertEquals(oldList.size(), newList.size());
    }

    /**
     * The list of users for a login organization must not include users created
     * on behalf of another user. Assert that the list size does not change when
     * creating on behalf users.
     * 
     * @throws Exception
     */
    @Test
    public void testGetUsersForOrganization_hideOnbehalfUsers()
            throws Exception {

        // given
        container.login(customerUser.getKey(), ROLE_ORGANIZATION_ADMIN);
        List<VOUserDetails> oldList = idService.getUsersForOrganization();

        // execute
        container.login(supplierAdminUser.getKey(), ROLE_SERVICE_MANAGER);
        idService.createOnBehalfUser(customer.getOrganizationId(), "pwd123");

        // assert
        container.login(customerUser.getKey(), ROLE_ORGANIZATION_ADMIN);
        List<VOUserDetails> newList = idService.getUsersForOrganization();
        assertEquals(oldList.size(), newList.size());
    }

    // --------------------------------------------------------------------
    // internal methods

    /**
     * Sets the last access time for the specified slave user.
     */
    private void setSlaveUserAccessTime(final VOUser user, final Long time)
            throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                PlatformUser user1 = dm.getReference(PlatformUser.class,
                        user.getKey());
                if (time != null) {
                    user1.getMaster().getDataContainer()
                            .setLastAccessTime(time.longValue());
                }
                return null;
            }
        });
    }

    /**
     * Validates that the new user was created properly and that all settings
     * are present.
     */
    private void checkCreatedUser(VOUserDetails createdUser,
            Organization userOrg, String password, boolean isSamlSpMode)
            throws Exception {
        assertNotNull(createdUser);
        if (isSamlSpMode) {
            assertEquals(password, createdUser.getUserId());
        }
        assertFalse(supplierAdminUser.getKey() == createdUser.getKey());
        assertEquals(userOrg.getOrganizationId(),
                createdUser.getOrganizationId());
        assertEquals(UserAccountStatus.ACTIVE, createdUser.getStatus());
        assertEquals(supplierAdminUser.getEmail(), createdUser.getEMail());
        assertEquals(supplierAdminUser.getLocale(), createdUser.getLocale());
        assertEquals(supplierAdminUser.getSalutation(),
                createdUser.getSalutation());
        assertTrue(createdUser.hasAdminRole());

        // correct the password, so implicitly verifying it
        if (!isSamlSpMode) {
            container.login(createdUser.getKey());
            idService.changePassword(password, password);
        }
    }

    /**
     * Initializes the test data.
     */
    private void initData() throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                createOrganizationRoles(dm);
                createPaymentTypes(dm);
                createUserRoles(dm);
                supplier = Organizations.createOrganization(dm,
                        OrganizationRoleType.SUPPLIER);
                UserGroup defaultGroupSup = new UserGroup();
                defaultGroupSup.setOrganization(supplier);
                defaultGroupSup.setIsDefault(true);
                defaultGroupSup.setName("default");
                dm.persist(defaultGroupSup);
                technologyProvider = Organizations.createOrganization(dm,
                        OrganizationRoleType.TECHNOLOGY_PROVIDER);
                UserGroup defaultGroupTP = new UserGroup();
                defaultGroupTP.setOrganization(technologyProvider);
                defaultGroupTP.setIsDefault(true);
                defaultGroupTP.setName("default");
                dm.persist(defaultGroupTP);
                tpAndSup = Organizations.createOrganization(dm,
                        OrganizationRoleType.TECHNOLOGY_PROVIDER,
                        OrganizationRoleType.SUPPLIER);
                supplierAdminUser = Organizations.createUserForOrg(dm, supplier,
                        true, "admin");
                supplier2 = Organizations.createOrganization(dm,
                        OrganizationRoleType.SUPPLIER);
                UserGroup defaultGroup = new UserGroup();
                defaultGroup.setOrganization(supplier2);
                defaultGroup.setIsDefault(true);
                defaultGroup.setName("default");
                dm.persist(defaultGroup);
                UserGroup defaultGroupTpAndSp = new UserGroup();
                defaultGroupTpAndSp.setOrganization(tpAndSup);
                defaultGroupTpAndSp.setIsDefault(true);
                defaultGroupTpAndSp.setName("default");
                dm.persist(defaultGroupTpAndSp);
                Organizations.createUserForOrg(dm, supplier2, true, "admin");
                customer = Organizations.createCustomer(dm, supplier);
                UserGroup defaultGroup1 = new UserGroup();
                defaultGroup1.setOrganization(customer);
                defaultGroup1.setIsDefault(true);
                defaultGroup1.setName("default");
                dm.persist(defaultGroup1);
                customer2 = Organizations.createCustomer(dm, supplier2);
                customerUser = Organizations.createUserForOrg(dm, customer,
                        true, "admin");
                OrganizationReference onBehalf = new OrganizationReference(
                        supplier, customer,
                        OrganizationReferenceType.ON_BEHALF_ACTING);
                dm.persist(onBehalf);
                onBehalf = new OrganizationReference(supplier, supplier2,
                        OrganizationReferenceType.ON_BEHALF_ACTING);
                dm.persist(onBehalf);
                onBehalf = new OrganizationReference(supplier,
                        technologyProvider,
                        OrganizationReferenceType.ON_BEHALF_ACTING);
                dm.persist(onBehalf);
                onBehalf = new OrganizationReference(supplier, tpAndSup,
                        OrganizationReferenceType.ON_BEHALF_ACTING);
                dm.persist(onBehalf);
                return null;
            }
        });
    }

}
