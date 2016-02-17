/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
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
 * History-Object of TriggerProcess, used for auditing. Will be automatically
 * created during persist, save or remove operations (if performed via
 * DataManager).
 * 
 * @author pock
 */
@Entity
@NamedQueries( { @NamedQuery(name = "TriggerProcessHistory.findByObject", query = "select h from TriggerProcessHistory h where h.objKey=:objKey order by objversion") })
public class TriggerProcessHistory extends
        DomainHistoryObject<TriggerProcessData> {

    private static final long serialVersionUID = 4843998695085146080L;

    public TriggerProcessHistory() {
        dataContainer = new TriggerProcessData();
    }

    /**
     * Constructs TriggerProcessHistory from a TriggerProcess domain object
     * 
     * @param td
     *            - the TriggerProcess
     */
    public TriggerProcessHistory(TriggerProcess tp) {
        super(tp);
        if (tp.getTriggerDefinition() != null) {
            setTriggerDefinitionObjKey(tp.getTriggerDefinition().getKey());
        }
        if (tp.getUser() != null) {
            setUserObjKey(Long.valueOf(tp.getUser().getKey()));
        }
    }

    /**
     * Reference to the TriggerDefinition (only key)
     */
    @Column(nullable = false)
    private long triggerDefinitionObjKey;

    /**
     * Reference to the PlatformUser (only key)
     */
    @Column
    private Long userObjKey;

    public long getTriggerDefinitionObjKey() {
        return triggerDefinitionObjKey;
    }

    public void setTriggerDefinitionObjKey(long triggerDefinitionObjKey) {
        this.triggerDefinitionObjKey = triggerDefinitionObjKey;
    }

    public Long getUserObjKey() {
        return userObjKey;
    }

    public void setUserObjKey(Long userObjKey) {
        this.userObjKey = userObjKey;
    }

}
