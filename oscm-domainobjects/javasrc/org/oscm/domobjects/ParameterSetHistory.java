/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: Peter Pock                                                      
 *                                                                              
 *  Creation Date: 29.06.2009                                                      
 *                                                                              
 *  Completion Time: 30.06.2009                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

/**
 * History object for ParameterSet
 * 
 * @author Peter Pock
 */
@Entity
@NamedQuery(name = "ParameterSetHistory.findByObject", query = "select c from ParameterSetHistory c where c.objKey=:objKey order by objversion")
public class ParameterSetHistory extends
        DomainHistoryObjectWithEmptyDataContainer {

    private static final long serialVersionUID = -7087423046294036547L;

    public ParameterSetHistory() {
        super();
    }

    public ParameterSetHistory(ParameterSet domObj) {
        super(domObj);
    }
}
