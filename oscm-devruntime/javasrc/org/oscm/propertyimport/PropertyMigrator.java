/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Dec 22, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.propertyimport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oscm.internal.types.enumtypes.ConfigurationKey;

/**
 * Migrator class for added new and deleting unneeded config settings.
 * 
 * @author miethaner
 */
public class PropertyMigrator {

    private static final String TABLE_NAME = "ConfigurationSetting";
    private static final String FIELD_TKEY = "tkey";
    private static final String FIELD_KEY = "information_id";
    private static final String FIELD_VALUE = "env_value";
    private static final String FIELD_CONTEXT = "context_id";
    private static final String FIELD_VERSION = "version";

    /**
     * parameters: driverClass driverURL userName userPwd [<contextId>]
     */
    public static void main(String args[]) {

        if (args.length < 4 || args.length > 5) {
            throw new RuntimeException(
                    "Usage: java PropertMigration <driverClass> <driverURL> <userName> <userPwd> [<contextId>]");
        }

        PropertyMigrator propertyImport = new PropertyMigrator(args[0], args[1],
                args[2], args[3], args.length >= 5 ? args[4] : null);
        propertyImport.execute();
    }

    private int count = 0;

    private String driverURL;
    private String userName;
    private String userPwd;
    private String contextId = "global";

    public PropertyMigrator(String driverClass, String driverURL,
            String userName, String userPwd, String contextId) {

        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "DriverClass '" + driverClass + "' could not be found");
        }

        this.driverURL = driverURL;
        this.userName = userName;
        this.userPwd = userPwd;
        if (contextId != null && contextId.trim().length() > 0) {
            this.contextId = contextId.trim();
        }
    }

    public void execute() {
        Connection conn;
        try {
            conn = getConnetion();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not connect to the database");
        }

        Map<String, String> settings = loadConfigurationSettings(conn);
        List<ConfigurationKey> missing = new ArrayList<>();

        try {
            initStartCount(conn);
            ConfigurationKey[] allKeys = ConfigurationKey.values();

            for (ConfigurationKey key : allKeys) {

                if (settings.containsKey(key.getKeyName())) {
                    settings.remove(key.getKeyName());
                } else {
                    missing.add(key);
                }
            }

            deleteUnusedEntries(conn, settings);
            addMissingEntries(conn, missing);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not close resources");
            }
        }
    }

    // protected to enable mocking in unit tests
    protected Connection getConnetion() throws SQLException {
        return DriverManager.getConnection(driverURL, userName, userPwd);
    }

    private Map<String, String> loadConfigurationSettings(Connection conn) {

        Map<String, String> settings = new HashMap<>();
        try {
            String query = "SELECT " + FIELD_KEY + ", " + FIELD_VALUE + " FROM "
                    + TABLE_NAME + " WHERE " + FIELD_CONTEXT + " = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, contextId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                settings.put(rs.getString(1), rs.getString(2));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load configuration settings",
                    e);
        }
        return settings;
    }

    private void initStartCount(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT MAX(" + FIELD_TKEY + ") FROM " + TABLE_NAME);
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Could not determine max id value in use");
        }
    }

    private void deleteUnusedEntries(Connection conn,
            Map<String, String> settings) {
        try {
            String query = "DELETE FROM " + TABLE_NAME + " WHERE " + FIELD_KEY
                    + " = ? AND " + FIELD_CONTEXT + " = ?";
            PreparedStatement stmt = conn.prepareStatement(query);

            for (String key : settings.keySet()) {

                stmt.setString(1, key);
                stmt.setString(2, contextId);

                stmt.executeUpdate();
                System.out.println("Delete Configuration " + key);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Unable to insert new configuration key");
        }
    }

    private void addMissingEntries(Connection conn,
            List<ConfigurationKey> missing) {

        try {
            String query = "INSERT INTO " + TABLE_NAME + "(" + FIELD_VALUE
                    + ", " + FIELD_KEY + ", " + FIELD_CONTEXT + ", "
                    + FIELD_TKEY + ", " + FIELD_VERSION
                    + ") VALUES(?, ?, ?, ?, 0)";
            PreparedStatement stmt = conn.prepareStatement(query);

            for (ConfigurationKey key : missing) {
                count++;

                stmt.setString(1, key.getFallBackValue());
                stmt.setString(2, key.getKeyName());
                stmt.setString(3, contextId);
                stmt.setLong(4, count);

                stmt.executeUpdate();
                System.out.println("Create Configuration " + key.getKeyName()
                        + " with value '" + key.getFallBackValue() + "'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Unable to insert new configuration key");
        }
    }
}
