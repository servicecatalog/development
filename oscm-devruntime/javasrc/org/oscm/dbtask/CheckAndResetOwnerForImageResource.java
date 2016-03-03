/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 2014年11月20日                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.dbtask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author yuyin
 * 
 */
public class CheckAndResetOwnerForImageResource {

    private final String driverURL;
    private final String userName;
    private final String userPwd;
    private final String owner;
    private Connection con;
    private static final int argNum = 5;

    public static void main(String args[]) throws Exception {

        if (args.length != argNum) {
            throw new RuntimeException(
                    "Usage: java PropertyImport <driverClass> <driverURL> <userName> <userPwd> ");
        }

        CheckAndResetOwnerForImageResource propertyImport = new CheckAndResetOwnerForImageResource(
                args[0], args[1], args[2], args[3], args[4]);
        propertyImport.execute();
    }

    public CheckAndResetOwnerForImageResource(String driverClass,
            String driverURL, String userName, String userPwd, String owner) {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("DriverClass '" + driverClass
                    + "' could not be found");
        }

        this.driverURL = driverURL;
        this.userName = userName;
        this.userPwd = userPwd;
        this.owner = owner;

    }

    public void execute() throws Exception {
        con = getConnection();
        updateEntries();

    }

    /**
     * reset BLOB when migration under postgres 9
     */
    private void updateEntries() {
        ResultSet rs;
        try {
            final String QUERY_IMAGERESOURCE_GETALL = "SELECT * FROM " + owner
                    + ".imageresource ir";
            rs = con.createStatement().executeQuery(QUERY_IMAGERESOURCE_GETALL);

            if (rs != null) {

                while (rs.next()) {

                    try {
                        rs.getByte("buffer");
                    } catch (SQLException e) {
                        String oid = rs.getString("buffer");
                        final PreparedStatement st = con
                                .prepareStatement("ALTER LARGE OBJECT " + oid
                                        + " OWNER TO " + owner + ";");
                        st.executeUpdate();
                    }

                }
            }
        } catch (SQLException e1) {
            // do not print anything as APP DB update or imageresource cannot
            // be found
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(driverURL, userName, userPwd);
    }
}
