/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: weiser                                                    
 *                                                                              
 *  Creation Date: 13.10.2010                                                      
 *                                                                              
 *  Completion Time: 13.10.2010                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.search.annotations.Indexed;
import org.oscm.domobjects.annotations.BusinessKey;
import org.oscm.types.enumtypes.UdaTargetType;
import org.oscm.internal.types.enumtypes.UdaConfigurationType;

/**
 * @author weiser
 * 
 */
@Indexed
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "organizationKey",
        "udaId", "targetType" }))
@NamedQueries({ @NamedQuery(name = "UdaDefinition.findByBusinessKey", query = "SELECT c FROM UdaDefinition c WHERE c.dataContainer.udaId=:udaId AND c.dataContainer.targetType=:targetType AND c.organizationKey=:organizationKey") })
@BusinessKey(attributes = { "organizationKey", "udaId", "targetType" })
public class UdaDefinition extends DomainObjectWithHistory<UdaDefinitionData> {

    private static final long serialVersionUID = -9012753323107429448L;

    @Column(name = "organizationKey", insertable = false, updatable = false, nullable = false)
    private long organizationKey;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organizationKey")
    private Organization organization;

    @OneToMany(mappedBy = "udaDefinition", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy
    private List<Uda> udas = new ArrayList<Uda>();

    public UdaDefinition() {
        setDataContainer(new UdaDefinitionData());
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        if (organization != null)
            setOrganizationKey(organization.getKey());
    }

    public String getUdaId() {
        return dataContainer.getUdaId();
    }

    public void setUdaId(String udaId) {
        dataContainer.setUdaId(udaId);
    }

    public String getDefaultValue() {
        return dataContainer.getDefaultValue();
    }

    public void setDefaultValue(String defaultValue) {
        dataContainer.setDefaultValue(defaultValue);
    }

    public UdaTargetType getTargetType() {
        return dataContainer.getTargetType();
    }

    public void setTargetType(UdaTargetType targetType) {
        dataContainer.setTargetType(targetType);
    }

    public long getOrganizationKey() {
        return organizationKey;
    }

    public void setOrganizationKey(long organizationKey) {
        this.organizationKey = organizationKey;
    }

    public List<Uda> getUdas() {
        return udas;
    }

    public void setUdas(List<Uda> udas) {
        this.udas = udas;
    }

    public void setConfigurationType(UdaConfigurationType configurationType) {
        dataContainer.setConfigurationType(configurationType);
    }

    public UdaConfigurationType getConfigurationType() {
        return dataContainer.getConfigurationType();
    }
}
