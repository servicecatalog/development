/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.propertyimport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.oscm.converter.PropertiesLoader;
import org.oscm.internal.types.enumtypes.AuthenticationMode;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.enumtypes.MandatoryAttributesInSamlSP;

public class PropertyImport {

    private static final String TABLE_NAME = "ConfigurationSetting";
    private static final String MAX_NUMBER_ALLOWED_USERS = "10";
    private static final String TIMER_INTERVAL_USER_COUNT = "43200000";

    /**
     * parameters: driverClass driverURL userName userPwd propertyFile
     * [<contextId>]
     */
    public static void main(String args[]) {

        if (args.length < 5 || args.length > 6) {
            throw new RuntimeException(
                    "Usage: java PropertyImport <driverClass> <driverURL> <userName> <userPwd> <propertyFile> [<overwriteFlag>] [<contextId>]");
        }

        PropertyImport propertyImport = new PropertyImport(args[0], args[1], args[2], args[3], args[4],
                args.length >= 6 ? Boolean.parseBoolean(args[5]) : false, args.length >= 7 ? args[6] : null);
        propertyImport.execute();
    }

    private int count = 0;

    private String driverURL;
    private String userName;
    private String userPwd;
    private String propertyFile;
    private boolean overwriteFlag;
    private String contextId = "global";

    public PropertyImport() {
    }

    public PropertyImport(String driverClass, String driverURL, String userName, String userPwd, String propertyFile,
            boolean overwriteFlag, String contextId) {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("DriverClass '" + driverClass + "' could not be found");
        }

        this.driverURL = driverURL;
        this.userName = userName;
        this.userPwd = userPwd;
        this.propertyFile = propertyFile;
        if (contextId != null && contextId.trim().length() > 0) {
            this.contextId = contextId.trim();
        }
        this.overwriteFlag = overwriteFlag;
    }

    public void execute() {
        Connection conn;
        try {
            conn = getConnetion();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not connect to the database");
        }

        final InputStream in;
        try {
            in = new FileInputStream(propertyFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not find resource file '" + propertyFile + "'.");
        }
        final Properties p = PropertiesLoader.loadProperties(in);

        try {
            initStartCount(conn);
            ConfigurationKey[] allKeys = ConfigurationKey.values();
            boolean isSamlSP = isSamlSPMode(p);
            for (ConfigurationKey key : allKeys) {
                String keyName = key.getKeyName();
                String value = (String) p.get(keyName);
                if (value == null || value.isEmpty()) {
                    if (keyName.equals(ConfigurationKey.MAX_NUMBER_ALLOWED_USERS.name())) {
                        value = MAX_NUMBER_ALLOWED_USERS;
                    }
                    if (keyName.equals(ConfigurationKey.TIMER_INTERVAL_USER_COUNT.name())) {
                        value = TIMER_INTERVAL_USER_COUNT;
                    }
                }

                if (value != null) {
                    if (key.getType() == ConfigurationKey.TYPE_BOOLEAN || key.getType() == ConfigurationKey.TYPE_LONG
                            || key.getType() == ConfigurationKey.TYPE_STRING
                            || key.getType() == ConfigurationKey.TYPE_PASSWORD) {

                        value = value.trim();
                    }
                }

                verifyValueValid(key, value, isSamlSP);
                if (value != null) {
                    verifyAuthMode(keyName, value);
                    verifyConfigurationValue(key, value);
                    writePropertyToDb(conn, key.getKeyName(), value);
                }
            }
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not close resources");
            }
        }
    }

    private void verifyValueValid(ConfigurationKey key, String value, boolean isSamlSP) {
        if (isNullValue(value) && (key.isMandatory() || isMandatoryAttributeInSamlSPMode(isSamlSP, key.getKeyName()))) {
            if (isMandatoryFallBack(key.getKeyName())) {
                value = key.getFallBackValue();
            } else {
                throw new RuntimeException("Mandatory attribute " + key.getKeyName() + " can not be set a null value");
            }
        }
    }

    private boolean isMandatoryFallBack(String keyName) {
        return (keyName.equals(ConfigurationKey.AUDIT_LOG_ENABLED.name())
                || keyName.equals(ConfigurationKey.AUDIT_LOG_MAX_ENTRIES_RETRIEVED.name()));
    }

    private boolean isMandatoryAttributeInSamlSPMode(boolean isSamlSP, String keyName) {
        return isSamlSP && verifyMandatoryInSamlSP(keyName);
    }

    private boolean isNullValue(String value) {
        return (value == null || value.isEmpty());
    }

    private boolean isSamlSPMode(Properties p) {
        ConfigurationKey[] allKeys = ConfigurationKey.values();
        boolean isSamlSPMode = false;
        for (ConfigurationKey key : allKeys) {
            String keyNameInConfig = key.getKeyName();
            String valueInConfig = (String) p.get(keyNameInConfig);

            if (!isNullValue(valueInConfig) && valueInConfig.equals(AuthenticationMode.SAML_SP.name())) {
                isSamlSPMode = true;
                break;
            }
        }
        return isSamlSPMode;
    }

    private boolean verifyMandatoryInSamlSP(String keyName) {
        boolean isMandatory = false;
        MandatoryAttributesInSamlSP[] attrs = MandatoryAttributesInSamlSP.values();
        for (MandatoryAttributesInSamlSP attr : attrs) {
            if (attr.name().equals(keyName)) {
                isMandatory = true;
                break;
            }
        }
        return isMandatory;
    }

    private void verifyAuthMode(String keyName, String value) {
        boolean isContained = false;
        if (ConfigurationKey.AUTH_MODE.name().equals(keyName)) {
            AuthenticationMode[] modes = AuthenticationMode.values();
            for (AuthenticationMode mode : modes) {
                if (mode.name().equals(value)) {
                    isContained = true;
                    break;
                }
            }
            if (!isContained) {
                throw new RuntimeException(
                        "Authentication mode has an invalid value - Allowed values are [INTERNAL, SAML_SP, SAML_IDP, OPENID_RP]");
            }
        }
    }

    private void verifyConfigurationValue(ConfigurationKey key, String value) {
        ConfigurationSettingsValidator.validate(key, value);
    }

    private void initStartCount(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(TKEY) FROM " + TABLE_NAME);
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not determine max id value in use.");
        }

    }

    private void writePropertyToDb(Connection con, String key, String property) {
        String query = null;
        if (getEntryCount(con, key) == 0) {
            count++;
            query = "INSERT INTO " + TABLE_NAME
                    + "(ENV_VALUE, INFORMATION_ID, CONTEXT_ID, TKEY, VERSION) VALUES(?, ?, ?," + count + ", 0)";
            System.out.println("Create Configuration " + key + " with value '" + property + "'");
        } else if (overwriteFlag) {
            query = "UPDATE " + TABLE_NAME + " SET ENV_VALUE = ? WHERE INFORMATION_ID = ? AND CONTEXT_ID = ?";
            System.out.println("Update Configuration " + key + " to value '" + property + "'");
        } else {
            System.out.println("Existing Configuration " + key + " skipped");
            return;
        }
        PreparedStatement stmt;
        try {
            stmt = con.prepareStatement(query);
            stmt.setString(1, property);
            stmt.setString(2, key);
            stmt.setString(3, contextId);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not write the properties to the database", e);
        }
    }

    private int getEntryCount(Connection con, String key) {
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE INFORMATION_ID = ? AND CONTEXT_ID = ?";
        PreparedStatement stmt;
        try {
            stmt = con.prepareStatement(query);
            stmt.setString(1, key);
            stmt.setString(2, contextId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not determine entry count of the table", e);
        }
        return count;
    }

    // protected to enable mocking in unit tests
    protected Connection getConnetion() throws SQLException {
        return DriverManager.getConnection(driverURL, userName, userPwd);
    }
}
