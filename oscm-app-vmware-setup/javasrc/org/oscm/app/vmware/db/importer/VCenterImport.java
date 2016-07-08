/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 2016-05-24
 *
 *******************************************************************************/

package org.oscm.app.vmware.db.importer;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VCenterImport extends GenericImport {

    private static final Logger logger = LoggerFactory
            .getLogger(VCenterImport.class);

    public static void main(String args[]) throws Exception {
        if (args.length < 5 || args.length > 6) {
            throw new RuntimeException(
                    "Usage: VCenterImport <driverClass> <driverURL> <userName> <userPwd> <csvFile>");
        }

        VCenterImport vcImport = new VCenterImport(args[0], args[1], args[2],
                args[3], args[4]);
        vcImport.execute();
    }

    public VCenterImport(String driverClass, String driverURL, String userName,
            String userPwd, String csvFile) {
        super(driverClass, driverURL, userName, userPwd, csvFile);
    }

    public void execute() throws Exception {
        VCenterCSV csv = null;

        try (Connection conn = getConnection();
                InputStream in = getFileInputStream();) {
            csv = new VCenterCSV(in);
            Map<String, String> line = csv.readNext();
            while (line != null) {
                String vcenterId = line.get(VCenterCSV.COL_VCENTER_IDENTIFIER);
                String vcenterName = line.get(VCenterCSV.COL_VCENTER_NAME);
                String url = line.get(VCenterCSV.COL_URL);
                String userid = line.get(VCenterCSV.COL_USER_ID);
                String password = line.get(VCenterCSV.COL_PASSWORD);
                String tkey = line.get(VCenterCSV.COL_TKEY);
                try {
                    addTableRow(conn, vcenterId, vcenterName, url, userid,
                            password, tkey);
                } catch (Exception e) {
                    logger.error("failed to add row:  " + tkey + " " + vcenterId
                            + " " + vcenterName);
                    logger.error(e.getMessage());
                    conn.rollback();
                    return;
                }
                try {
                    createSequence(conn, vcenterId);
                } catch (Exception e) {
                    logger.error("failed to create sequence for   " + tkey + " "
                            + vcenterId + " " + vcenterName);
                    logger.error(e.getMessage());
                    conn.rollback();
                    return;
                }
                try {
                    line = csv.readNext();
                } catch (Exception e) {
                    conn.rollback();
                    logger.error("Failed to read line from CSV file after row: "
                            + tkey + " " + vcenterId + " " + vcenterName);
                    logger.error(e.getMessage());
                    return;
                }
            }
            conn.commit();
        } catch (Exception e) {
            logger.error("failed to import vcenter settings.", e);
            throw e;
        } finally {
            try {
                if (csv != null) {
                    csv.close();
                }
            } catch (Exception e) {
                logger.error("Failed to close resources", e);
                throw e;
            }
        }
    }

    private void createSequence(Connection con, String vcenterId)
            throws Exception {
        String query = "create sequence vcenter_" + vcenterId.trim()
                + "_seq increment 1 minvalue 1 maxvalue 10000 start 1 cycle";
        try (PreparedStatement stmt = con.prepareStatement(query);) {
            stmt.execute();
        }
    }

    private void addTableRow(Connection con, String vcId, String vcName,
            String url, String userid, String password, String tkey)
            throws Exception {

        String query = "insert into vcenter (TKEY, NAME, IDENTIFIER, URL, USERID, PASSWORD) values(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(query);) {
            stmt.setInt(1, Integer.parseInt(tkey));
            stmt.setString(2, vcName);
            stmt.setString(3, vcId);
            stmt.setString(4, url);
            stmt.setString(5, userid);
            stmt.setString(6, password);
            stmt.execute();
        }
    }

}
