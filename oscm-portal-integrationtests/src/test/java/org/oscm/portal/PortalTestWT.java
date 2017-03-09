package org.oscm.portal;

        import static org.junit.Assert.assertNotNull;
        import static org.junit.Assert.assertTrue;

        import org.junit.AfterClass;
        import org.junit.BeforeClass;
        import org.junit.FixMethodOrder;
        import org.junit.Test;
        import org.junit.runners.MethodSorters;
        import org.oscm.webtest.WebTester;

/**
 * Integration web test to create an organization.
 *
 * @author poissond
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PortalTestWT {

    private static final String USER = "administrator";
    private static final String PASSWORD = "admin123";

    private static final String ORG = "mp_owner_31";
    private static final String ORG_ADMIN = ORG + "_admin";

    private static final int PASSWORD_LENGTH = 8;

    private static WebTester tester;
    private static String password;

    @BeforeClass
    public static void setup() throws Exception {
        tester = new WebTester();
        tester.visitPortal("");

    }

    @AfterClass
    public static void cleanUp() {
        tester.close();
    }

    @Test
    public void test01Create() {
        assertTrue(tester.getCurrentUrl() == "about:blank");
    }
}
