/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Author: Mike J&auml;ger                                                    
 *                                                                              
 *  Creation Date: 06.05.2009                                                      
 *                                                                              
 *  Completion Time: 17.06.2009                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

/**
 * History-Object of Category.
 * 
 * @author Mani Afschar
 * 
 */
@Entity
@NamedQuery(name = "CategoryHistory.findByObject", query = "select c from CategoryHistory c where c.objKey=:objKey order by objversion")
public class CategoryHistory extends DomainHistoryObject<CategoryData> {

    private static final long serialVersionUID = -126472952008443314L;

    private Long marketplaceObjKey;

    public CategoryHistory() {
        dataContainer = new CategoryData();
    }

    /**
     * Constructs EventHistory from an Event domain object
     * 
     * @param c
     *            The Event
     */
    public CategoryHistory(Category c) {
        super(c);
        if (c.getMarketplace() != null) {
            this.marketplaceObjKey = Long.valueOf(c.getMarketplace().getKey());
        }
    }

    public Long getMarketplaceObjKey() {
        return marketplaceObjKey;
    }

    public void setMarketplaceObjKey(Long marketplaceObjKey) {
        this.marketplaceObjKey = marketplaceObjKey;
    }

}
