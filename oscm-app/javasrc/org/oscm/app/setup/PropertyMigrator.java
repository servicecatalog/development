/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Dec 22, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.setup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.oscm.app.domain.PlatformConfigurationKey;

/**
 * @author miethaner
 *
 */
public class PropertyMigrator {

    private static final String TABLE_NAME = "ConfigurationSetting";
    private static final String FIELD_KEY = "settingkey";
    private static final String FIELD_VALUE = "settingvalue";
    private static final String FIELD_CONTROLLER = "controllerid";

    /**
     * parameters: driverClass driverURL userName userPwd [<contextId>]
     */
    public static void main(String args[]) {

        if (args.length < 4 || args.length > 5) {
            throw new RuntimeException(
                    "Usage: java PropertMigration <driverClass> <driverURL> <userName> <userPwd> [<controllerId>]");
        }

        PropertyMigrator propertyImport = new PropertyMigrator(args[0], args[1],
                args[2], args[3], args.length >= 5 ? args[4] : null);
        propertyImport.execute();
    }

    private String driverURL;
    private String userName;
    private String userPwd;
    private String controllerId = "PROXY";

    public PropertyMigrator(String driverClass, String driverURL,
            String userName, String userPwd, String controllerId) {

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
        if (controllerId != null && controllerId.trim().length() > 0) {
            this.controllerId = controllerId.trim();
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

        Properties p = loadConfigurationSettings(conn);
        List<PlatformConfigurationKey> missing = new ArrayList<>();

        try {
            PlatformConfigurationKey[] allKeys = PlatformConfigurationKey
                    .values();

            for (PlatformConfigurationKey key : allKeys) {

                if (p.contains(key.name())) {
                    p.remove(key.name());
                } else {
                    missing.add(key);
                }
            }

            deleteUnusedEntries(conn, p);
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

    private Properties loadConfigurationSettings(Connection conn) {

        Properties props = new Properties();
        try {
            String query = "SELECT " + FIELD_KEY + ", " + FIELD_VALUE + " FROM "
                    + TABLE_NAME + " WHERE " + FIELD_CONTROLLER + " = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, controllerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                props.put(rs.getString(1), rs.getString(2));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load configuration settings",
                    e);
        }
        return props;
    }

    private void deleteUnusedEntries(Connection conn, Properties p) {
        try {
            String query = "DELETE FROM " + TABLE_NAME + " WHERE " + FIELD_KEY
                    + " = ? AND " + FIELD_CONTROLLER + " = ?";
            PreparedStatement stmt = conn.prepareStatement(query);

            for (String key : p.stringPropertyNames()) {

                stmt.setString(1, key);
                stmt.setString(2, controllerId);

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
            List<PlatformConfigurationKey> missing) {

        try {
            String query = "INSERT INTO " + TABLE_NAME + "(" + FIELD_VALUE
                    + ", " + FIELD_KEY + ", " + FIELD_CONTROLLER + ", "
                    + ") VALUES(?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);

            for (PlatformConfigurationKey key : missing) {
                stmt.setString(1, "change");
                stmt.setString(2, key.name());
                stmt.setString(3, controllerId);

                stmt.executeUpdate();
                System.out.println("Create Configuration " + key.name()
                        + " with value '" + "change" + "'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Unable to insert new configuration key");
        }
    }
}
