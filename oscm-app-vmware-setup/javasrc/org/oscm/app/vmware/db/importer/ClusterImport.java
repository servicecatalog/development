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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterImport extends GenericImport {

    private static final Logger logger = LoggerFactory
            .getLogger(ClusterImport.class);

    public static void main(String args[]) throws Exception {

        if (args.length < 5 || args.length > 6) {
            throw new RuntimeException(
                    "Usage: ClusterImport <driverClass> <driverURL> <userName> <userPwd> <csvFile>");
        }

        ClusterImport clusterImport = new ClusterImport(args[0], args[1],
                args[2], args[3], args[4]);
        clusterImport.execute();
    }

    private final Map<String, Integer> datacenterCache = new HashMap<String, Integer>();

    public ClusterImport(String driverClass, String driverURL, String userName,
            String userPwd, String csvFile) {
        super(driverClass, driverURL, userName, userPwd, csvFile);
    }

    public void execute() throws Exception {
        ClusterCSV csv = null;
        try (Connection conn = getConnection();
                InputStream in = getFileInputStream();) {
            csv = new ClusterCSV(in);
            Map<String, String> line = csv.readNext();
            int lineCount = 0;
            while (line != null) {
                lineCount++;
                String clusterName = line.get(ClusterCSV.COL_CLUSTER_NAME);
                String vcenter = line.get(ClusterCSV.COL_VCENTER);
                String datacenter = line.get(ClusterCSV.COL_DATACENTER);
                String hosts = line.get(ClusterCSV.COL_BLACKLIST_HOSTS);
                String storages = line.get(ClusterCSV.COL_BLACKLIST_STORAGES);
                try {
                    int dcKey = getDatacenterKey(conn, vcenter, datacenter);
                    String condition = "datacenter_tkey=" + dcKey
                            + " AND name='" + clusterName + "'";
                    if (entryExists(conn, "cluster", condition)) {
                        logger.debug("cluster.csv  Skipping line " + lineCount
                                + " because cluster already exists. " + vcenter
                                + ", " + datacenter + ", " + clusterName);
                    } else {
                        addTableRow(conn, vcenter, clusterName, dcKey, hosts,
                                storages);
                    }
                } catch (Exception e) {
                    logger.error("failed to add row:  " + vcenter + " "
                            + datacenter + " " + clusterName);
                    logger.error(e.getMessage());
                    conn.rollback();
                    throw e;
                }
                try {
                    line = csv.readNext();
                } catch (Exception e) {
                    logger.error("Failed to read line from CSV file after row: "
                            + vcenter + " " + datacenter + " " + clusterName);
                    logger.error(e.getMessage());
                    conn.rollback();
                    throw e;
                }
            }
            conn.commit();
        } catch (Exception e) {
            logger.error("failed to import cluster.", e);
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

    private void addTableRow(Connection con, String vcenter, String clusterName,
            int dcKey, String hosts, String storages) throws Exception {

        StringBuilder loadbalancer = new StringBuilder(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><essvcenter><balancer class=\"org.oscm.app.vmware.business.balancer.DynamicEquipartitionHostBalancer\" cpuWeight=\"\" memoryWeight=\"\" vmWeight=\"\" >");
        if (hosts != null && !"".equals(hosts)) {
            String[] hosta = hosts.split("#");
            for (String host : hosta) {
                loadbalancer
                        .append("<blacklisthost>" + host + "</blacklisthost>");
            }

        }
        if (storages != null && !"".equals(storages)) {
            String[] storagea = storages.split("#");
            for (String storage : storagea) {
                loadbalancer.append(
                        "<blackliststorage>" + storage + "</blackliststorage>");
            }

        }

        loadbalancer.append("</balancer></essvcenter>");

        String query = "insert into cluster (TKEY, NAME, LOAD_BALANCER, DATACENTER_TKEY) values (DEFAULT, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query);) {
            stmt.setString(1, clusterName);
            stmt.setString(2, loadbalancer.toString());
            stmt.setInt(3, dcKey);
            stmt.execute();
        }
    }

    private int getDatacenterKey(Connection con, String vcenter,
            String datacenter) {
        int dcKey = -1;

        if (datacenterCache.containsKey(datacenter)) {
            return datacenterCache.get(datacenter).intValue();
        }

        String query = "SELECT tkey FROM datacenter WHERE name = ? and vcenter_tkey = (SELECT tkey FROM vcenter WHERE name = ?)";
        try (PreparedStatement stmt = con.prepareStatement(query);) {
            stmt.setString(1, datacenter);
            stmt.setString(2, vcenter);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dcKey = rs.getInt(1);
                datacenterCache.put(datacenter, Integer.valueOf(dcKey));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Failed to retrieve datacenter " + datacenter, e);
        }

        if (dcKey == -1) {
            throw new RuntimeException(
                    "Failed to retrieve datacenter " + datacenter);
        }
        return dcKey;
    }
}
