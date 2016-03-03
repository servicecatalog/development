/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: kulle                                                  
 *                                                                              
 *  Creation Date: 01.12.2011                                                      
 *                                                                              
 *  Completion Time: 01.12.2011                                        
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

/**
 * HistoryObject of MarketingPermission
 * 
 * @author kulle
 */
@Entity
@NamedQuery(name = "MarketingPermissionHistory.findByObject", query = "select c from MarketingPermissionHistory c where c.objKey=:objKey order by objversion")
public class MarketingPermissionHistory extends
        DomainHistoryObjectWithEmptyDataContainer {

    private static final long serialVersionUID = -7703087003895246930L;

    private long technicalProductObjKey;

    private long organizationReferenceObjKey;

    /**
     * The default constructor.
     */
    public MarketingPermissionHistory() {

    }

    /**
     * Constructs a history object for the given marketing permission.
     * 
     * @param domObj
     *            The marketing permission
     */
    public MarketingPermissionHistory(MarketingPermission domObj) {
        super(domObj);
        if (domObj != null) {
            technicalProductObjKey = domObj.getTechnicalProduct().getKey();
            organizationReferenceObjKey = domObj.getOrganizationReference()
                    .getKey();
        }
    }

    public long getTechnicalProductObjKey() {
        return technicalProductObjKey;
    }

    public void setTechnicalProductObjKey(long technicalProductObjKey) {
        this.technicalProductObjKey = technicalProductObjKey;
    }

    public long getOrganizationReferenceObjKey() {
        return organizationReferenceObjKey;
    }

    public void setOrganizationReferenceObjKey(long organizationReferenceObjKey) {
        this.organizationReferenceObjKey = organizationReferenceObjKey;
    }

}
