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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.search.annotations.Indexed;
import org.oscm.domobjects.annotations.BusinessKey;

/**
 * The uda domain object with its data container and a reference to its parent
 * {@link UdaDefinition}.
 * 
 * @author weiser
 * 
 */
@Indexed
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
        "udaDefinitionKey", "targetObjectKey" }))
@NamedQueries({
        @NamedQuery(name = "Uda.findByBusinessKey", query = "SELECT c FROM Uda c WHERE c.dataContainer.targetObjectKey=:targetObjectKey AND c.udaDefinitionKey=:udaDefinitionKey"),
        @NamedQuery(name = "Uda.getByTargetTypeAndKey", query = "SELECT c FROM Uda c WHERE c.dataContainer.targetObjectKey=:targetKey AND c.udaDefinition.dataContainer.targetType=:targetType"),
        @NamedQuery(name = "Uda.getByTypeAndKeyForSupplier", query = "SELECT c FROM Uda c WHERE c.dataContainer.targetObjectKey=:targetKey AND c.udaDefinition.dataContainer.targetType=:targetType AND c.udaDefinition.organizationKey=:supplierKey ORDER BY c.key ASC"),
        @NamedQuery(name = "Uda.getAllForCustomerBySupplier", query = "SELECT c FROM Uda c WHERE ((c.dataContainer.targetObjectKey=:subKey AND c.udaDefinition.dataContainer.targetType=:subType) OR (c.dataContainer.targetObjectKey=:custKey AND c.udaDefinition.dataContainer.targetType=:custType)) AND c.udaDefinition.dataContainer.configurationType IN (:configTypes) AND c.udaDefinition.organizationKey=:supplierKey") })
@BusinessKey(attributes = { "udaDefinitionKey", "targetObjectKey" })
public class Uda extends DomainObjectWithHistory<UdaData> {

    private static final long serialVersionUID = -7197165490507315844L;

    @Column(name = "udaDefinitionKey", insertable = false, updatable = false, nullable = false)
    private long udaDefinitionKey;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "udaDefinitionKey")
    private UdaDefinition udaDefinition;

    public Uda() {
        setDataContainer(new UdaData());
    }

    public UdaDefinition getUdaDefinition() {
        return udaDefinition;
    }

    public void setUdaDefinition(UdaDefinition udaDefinition) {
        this.udaDefinition = udaDefinition;
        if (udaDefinition != null) {
            setUdaDefinitionKey(udaDefinition.getKey());
        }
    }

    public long getTargetObjectKey() {
        return dataContainer.getTargetObjectKey();
    }

    public void setTargetObjectKey(long targetObjectKey) {
        dataContainer.setTargetObjectKey(targetObjectKey);
    }

    public void setTargetObjectKey(
            DomainObject<? extends DomainDataContainer> domObj) {
        setTargetObjectKey(domObj.getKey());
    }

    public String getUdaValue() {
        return dataContainer.getUdaValue();
    }

    public void setUdaValue(String udaValue) {
        dataContainer.setUdaValue(udaValue);
    }

    public long getUdaDefinitionKey() {
        return udaDefinitionKey;
    }

    public void setUdaDefinitionKey(long udaDefinitionKey) {
        this.udaDefinitionKey = udaDefinitionKey;
    }

}
