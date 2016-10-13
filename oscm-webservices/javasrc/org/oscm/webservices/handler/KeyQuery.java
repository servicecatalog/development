/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 14.08.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.webservices.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

public class KeyQuery extends AbstractQuery {

    private String userId = null;
    private long userKey = -1;

    public KeyQuery(DataSource ds, String userId) {
        super(ds);
        this.userId = userId;
    }

    @Override
    protected String getStatement() {
        return "SELECT pu.tkey FROM PlatformUser pu JOIN organization o on pu.organizationkey=o.tkey where o.tenant_tkey is null and pu.userid=?";
    }

    @Override
    protected void setParameters(PreparedStatement p) throws SQLException {
        try {
            p.setString(1, userId);
        } catch (NumberFormatException e) {
            p.setNull(1, Types.BIGINT);
        }
    }

    @Override
    protected void mapResult(ResultSet rs) throws SQLException {
        userKey = rs.getLong(1);
    }

    public long getUserKey() {
        return userKey;
    }
}
