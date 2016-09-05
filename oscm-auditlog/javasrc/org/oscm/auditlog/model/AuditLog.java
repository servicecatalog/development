/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.auditlog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;

@NamedQueries({
        @NamedQuery(name = "AuditLog.findByDateRange", query = "SELECT o FROM AuditLog o  WHERE creationTime >= :startTime AND creationTime < :endTime ORDER BY creationTime"),
        @NamedQuery(name = "AuditLog.countByDateRange", query = "SELECT COUNT(o) FROM AuditLog o WHERE creationTime >= :startTime AND creationTime < :endTime"),
        @NamedQuery(name = "AuditLog.countByOperationAndDateRange", query = "SELECT COUNT(o) FROM AuditLog o WHERE o.operationId in (:operationIds) AND creationTime >= :startTime AND creationTime < :endTime"),
        @NamedQuery(name = "AuditLog.findByOperationAndDateRange", query = "SELECT o FROM AuditLog o  WHERE o.operationId in (:operationIds) AND creationTime >= :startTime AND creationTime < :endTime ORDER BY creationTime") })
@Entity
public class AuditLog implements AuditLogEntry {
    @Id
    @Column(name = "tkey")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "auditlog_seq")
    @TableGenerator(table = "hibernate_sequences", name = "auditlog_seq", allocationSize = 100)
    private long key;

    @Column(nullable = false)
    private String operationId;

    @Column(nullable = false)
    private String operationName;

    @Column(nullable = false)
    private String log;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String organizationId;

    @Column(nullable = false)
    private String organizationName;

    @Column(nullable = false)
    private long creationTime;

    public AuditLog() {
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    @Override
    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    @Override
    public String getOperationName() {
        return operationName;
    }

    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public String getLog() {
        return log;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    @Override
    public String getOrganizationName() {
        return organizationName;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}
