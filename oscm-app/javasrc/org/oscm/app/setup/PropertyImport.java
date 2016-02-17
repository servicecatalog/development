/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.app.setup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import org.oscm.string.Strings;
import org.oscm.app.domain.PlatformConfigurationKey;

/**
 * Command line tool allowing to import configuration settings from a file into
 * a configuration table.
 * 
 * @author Dirk Bernsau
 * 
 */
public class PropertyImport {

	private static final String TABLE_NAME = "ConfigurationSetting";
	private static final String CONTROLLER_ID_PROXY = "PROXY";
	private static final String CONTROLLER_ID_KEY = "CONTROLLER_ID";

	static final String ERR_PARAM = "{0}";
	static final String ERR_PARAM_ESC = "\\{0\\}";
	static final String ERR_USAGE = "Usage: java PropertyImport <driverClass> <driverURL> <userName> <userPwd> <propertyFile> [<overwriteFlag> [<controllerId>]]";
	static final String ERR_DRIVER_CLASS_NULL = "DriverClass not specified";
	static final String ERR_DRIVER_CLASS_NOT_FOUND = "DriverClass '"
			+ ERR_PARAM + "' could not be found";
	static final String ERR_INPUT_STREAM = "Input stream could not be closed properly.";
	static final String ERR_DB_CONNECTION = "Could not connect to database or connection could not released properly.";
	static final String ERR_FILE_NOT_FOUND = "Could not find resource file '"
			+ ERR_PARAM + "'.";
	static final String ERR_CONTROLLER_ID_EMPTY = "Controller id not set or empty.";
	static final String ERR_CONTROLLER_ID_RESERVED = "Controller id '"
			+ CONTROLLER_ID_PROXY
			+ "' is reserved for the Asynchronous Provisioning Proxy settings.";
	static final String ERR_DB_WRITE = "Could not write the properties to the database.";
	static final String ERR_DB_ENTRY_COUNT = "Could not determine entry count of the table.";
	static final String ERR_PROP_LOAD = "Could not load properties from file.";
	static final String ERR_MANDATORY_ATTRIBUTE = "Mandatory attribute %s can not be set a null value.";

	/**
	 * parameters: driverClass driverURL userName userPwd propertyFile
	 * [<contextId>]
	 */
	public static void main(String args[]) {

		if (args == null || args.length < 5 || args.length > 7) {
			throw new RuntimeException(ERR_USAGE);
		}

		PropertyImport propertyImport = new PropertyImport(args[0], args[1],
				args[2], args[3], args[4],
				args.length >= 6 ? Boolean.parseBoolean(args[5]) : false,
				args.length >= 7 ? args[6] : null);
		propertyImport.execute();
	}

	private String driverURL;
	private String userName;
	private String userPwd;
	private String propertyFile;
	private boolean overwriteFlag;
	private String controllerId;

	public PropertyImport(String driverClass, String driverURL,
			String userName, String userPwd, String propertyFile,
			boolean overwriteFlag, String controllerId) {
		if (driverClass == null) {
			throw new RuntimeException(ERR_DRIVER_CLASS_NULL);
		}
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(ERR_DRIVER_CLASS_NOT_FOUND.replaceFirst(
					ERR_PARAM_ESC, driverClass));
		}

		this.driverURL = driverURL;
		this.userName = userName;
		this.userPwd = userPwd;
		this.propertyFile = propertyFile;
		if (controllerId != null && controllerId.trim().length() > 0) {
			this.controllerId = controllerId.trim();
		} else {
			this.controllerId = CONTROLLER_ID_PROXY;
		}
		this.overwriteFlag = overwriteFlag;
	}

	public void execute() {
		try (Connection conn = getConnetion();
				InputStream in = getInputStreamForProperties();) {
			Properties p = loadProperties(in);
			if (CONTROLLER_ID_PROXY.equals(controllerId)) {
				importProxySettings(conn, p);
			} else {
				importControllerSettings(conn, p);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(ERR_FILE_NOT_FOUND.replaceFirst(
					ERR_PARAM_ESC, propertyFile));
		} catch (IOException e1) {
			throw new RuntimeException(ERR_INPUT_STREAM, e1);
		} catch (SQLException e2) {
			throw new RuntimeException(ERR_DB_CONNECTION, e2);
		}
	}

	void importControllerSettings(Connection conn, Properties p) {
		Set<Object> keys = p.keySet();
		boolean controllerIdSet = false;
		for (Object key : keys) {
			if (CONTROLLER_ID_KEY.equals(key)) {
				String controllerIdValue = (String) p.get(key);
				if (CONTROLLER_ID_PROXY.equals(controllerIdValue)) {
					throw new RuntimeException(ERR_CONTROLLER_ID_RESERVED);
				} else {
					controllerId = controllerIdValue;
					controllerIdSet = true;
					break;
				}
			}
		}
		if (controllerIdSet && Strings.isEmpty(controllerId)) {
			controllerIdSet = false;
		}
		if (!controllerIdSet) {
			throw new RuntimeException(ERR_CONTROLLER_ID_EMPTY);
		}
		for (Object key : keys) {
			if (!CONTROLLER_ID_KEY.equals(key)) {
				writePropertyToDb(conn, (String) key, trimValue((String) p.get(key)));
			}
		}
	}

	void importProxySettings(Connection conn, Properties p) {
		PlatformConfigurationKey[] allKeys = PlatformConfigurationKey.values();
		Properties currentProxyProps = loadConfigurationSettings(conn);

		for (PlatformConfigurationKey key : allKeys) {
			String value = (String) p.get(key.name());
			value=trimValue(value);			
			if ((value == null || value.isEmpty()) && key.isMandatory()) {
				if (currentProxyProps.get(key.name()) == null) {
					throw new RuntimeException(String.format(ERR_MANDATORY_ATTRIBUTE, key.name()));
				}
			}
			if (value != null) {
				verifyConfigurationValue(key, value);
				writePropertyToDb(conn, key.name(), value);
			}
		}
	}
	
	String trimValue(String value){
	    String trimValue=value;
	    if(value!=null){	     
	        trimValue=value.trim();
	    }
	    return trimValue;
	}

	private void verifyConfigurationValue(PlatformConfigurationKey key,
			String value) {
		ConfigurationSettingsValidator.validate(key, value);
	}

	protected void writePropertyToDb(Connection con, String key, String property) {
		String query = null;
		if (getEntryCount(con, key) == 0) {
			query = "INSERT INTO "
					+ TABLE_NAME
					+ "(settingvalue, settingkey, controllerid) VALUES(?, ?, ?)";
			System.out.println("Create Configuration " + key + " with value '"
					+ property + "'");
		} else if (overwriteFlag) {
			query = "UPDATE "
					+ TABLE_NAME
					+ " SET settingvalue = ? WHERE settingkey = ? AND controllerid = ?";
			System.out.println("Update Configuration " + key + " to value '"
					+ property + "'");
		} else {
			System.out.println("Existing Configuration " + key + " skipped");
			return;
		}

		try {
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, property);
			stmt.setString(2, key);
			stmt.setString(3, controllerId);
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(ERR_DB_WRITE, e);
		}
	}

	private Properties loadConfigurationSettings(Connection con) {

		Properties props = new Properties();
		try {
			String query = "SELECT * FROM " + TABLE_NAME
					+ " WHERE controllerid = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, controllerId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				props.put(rs.getString(1), rs.getString(2));
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(ERR_DB_ENTRY_COUNT, e);
		}
		return props;
	}

	private int getEntryCount(Connection con, String key) {

		int count = 0;
		try {
			String query = "SELECT COUNT(*) FROM " + TABLE_NAME
					+ " WHERE settingkey = ? AND controllerid = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, key);
			stmt.setString(2, controllerId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(ERR_DB_ENTRY_COUNT, e);
		}
		return count;
	}

	protected Properties loadProperties(final InputStream in) {
		final Properties properties = new Properties();
		try {
			try {
				properties.load(in);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(ERR_PROP_LOAD, e);
		}
		return properties;
	}

	protected FileInputStream getInputStreamForProperties()
			throws FileNotFoundException {
		return new FileInputStream(propertyFile);
	}

	protected Connection getConnetion() throws SQLException {
		return DriverManager.getConnection(driverURL, userName, userPwd);
	}

	protected String getDriverURL() {
		return driverURL;
	}

	protected String getUserName() {
		return userName;
	}

	protected String getUserPwd() {
		return userPwd;
	}

	protected String getPropertyFile() {
		return propertyFile;
	}

	protected boolean isOverwriteFlag() {
		return overwriteFlag;
	}

	protected String getControllerId() {
		return controllerId;
	}
}
