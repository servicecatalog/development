/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Author: Peter Pock                                 
 *                                                                              
 *  Creation Date: 14.06.2010                                                      
 *                                                                              
 *  Completion Time: 15.06.2010                                        
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * History-Object of TriggerDefinition, used for auditing. Will be automatically
 * created during persist, save or remove operations (if performed via
 * DataManager).
 * 
 * @author pock
 */
@Entity
@NamedQueries( { @NamedQuery(name = "TriggerDefinitionHistory.findByObject", query = "select h from TriggerDefinitionHistory h where h.objKey=:objKey order by objversion") })
public class TriggerDefinitionHistory extends
        DomainHistoryObject<TriggerDefinitionData> {

    private static final long serialVersionUID = 1171688373610102252L;

    public TriggerDefinitionHistory() {
        dataContainer = new TriggerDefinitionData();
    }

    /**
     * Constructs TriggerDefinitionHistory from a TriggerDefinition domain
     * object
     * 
     * @param td
     *            - the TriggerDefinition
     */
    public TriggerDefinitionHistory(TriggerDefinition td) {
        super(td);
        if (td.getOrganization() != null) {
            setOrganizationObjKey(td.getOrganization().getKey());
        }
    }

    /**
     * Reference to the Organization (only key)
     */
    @Column(nullable = false)
    private long organizationObjKey;

    public void setOrganizationObjKey(long organization_objid) {
        this.organizationObjKey = organization_objid;
    }

    public long getOrganizationObjKey() {
        return organizationObjKey;
    }

}
