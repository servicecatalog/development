/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Dec 23, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.setup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oscm.app.db.TestDatabase;
import org.oscm.app.domain.PlatformConfigurationKey;

/**
 * Integration test for PropertyMigrator
 * 
 * @author miethaner
 */
public class PropertyMigratorIT {

    private static TestDatabase testDatabase;

    private class TestMigrator extends PropertyMigrator {

        public TestMigrator(String driverClass, String driverURL,
                String userName, String userPwd, String contextId) {
            super(driverClass, driverURL, userName, userPwd, contextId);
        }

        @Override
        protected Connection getConnetion() throws SQLException {
            System.out.println("test");
            return testDatabase.getDBconnection();
        }
    }

    @BeforeClass
    public static void initDb() throws Exception {
        testDatabase = new TestDatabase();
        testDatabase.initDatabase();
    }

    @Before
    public void setUp() throws Exception {
        testDatabase.insertData(
                getClass().getResource("/setup_PropertyMigrator.xml"));
    }

    @After
    public void tearDown() throws Exception {
        testDatabase.clean();
    }

    @AfterClass
    public static void cleanupDB() throws Exception {
        testDatabase.updateDBSchemaToLatestVersion();
    }

    @Test
    public void testParameters() {

        try {
            TestMigrator
                    .main(new String[] { "java.lang.String", "url", "user" });
            fail();
        } catch (Exception e) {
        }

        try {
            TestMigrator.main(new String[] { "java.lang.String", "url", "user",
                    "password", "context", "test" });
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testMigration() throws Exception {
        TestMigrator migrator = new TestMigrator("java.lang.String", "url",
                "user", "password", "PROXY");
        migrator.execute();

        Connection conn = testDatabase.getDBconnection();
        String query = "SELECT settingkey, settingvalue FROM configurationsetting WHERE controllerid = 'PROXY'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        Map<String, String> settings = new HashMap<>();
        while (rs.next()) {
            settings.put(rs.getString(1), rs.getString(2));
        }

        assertEquals(PlatformConfigurationKey.values().length, settings.size());

        for (PlatformConfigurationKey ck : PlatformConfigurationKey.values()) {
            assertTrue(settings.containsKey(ck.name()));
        }

        assertTrue(settings.get(PlatformConfigurationKey.BSS_USER_PWD.name())
                .startsWith("_crypt:"));
    }
}
