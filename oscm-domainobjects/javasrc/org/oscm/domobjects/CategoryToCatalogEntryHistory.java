/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                                                                                                                                               
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "CategoryToCatalogEntryHistory.findByObject", query = "SELECT c FROM CategoryToCatalogEntryHistory c WHERE c.objKey=:objKey ORDER BY objversion")
public class CategoryToCatalogEntryHistory extends
        DomainHistoryObjectWithEmptyDataContainer {

    private static final long serialVersionUID = 5101082676087797637L;

    /**
     * Reference to the target category (key-only).
     */
    @Column(name = "category_tkey")
    private long categoryKey;

    /**
     * Reference to the target catalogEntry (key-only).
     */
    @Column(name = "catalogentry_tkey")
    private long catalogEntryKey;

    public CategoryToCatalogEntryHistory() {

    }

    public CategoryToCatalogEntryHistory(CategoryToCatalogEntry cce) {
        super(cce);
        if (cce.getCategory() != null) {
            this.setCategoryKey(cce.getCategory().getKey());
        }
        if (cce.getCatalogEntry() != null) {
            this.setCatalogEntryKey(cce.getCatalogEntry().getKey());
        }
    }

    public void setCategoryKey(long categoryKey) {
        this.categoryKey = categoryKey;
    }

    public long getCategoryKey() {
        return categoryKey;
    }

    public void setCatalogEntryKey(long catalogEntryKey) {
        this.catalogEntryKey = catalogEntryKey;
    }

    public long getCatalogEntryKey() {
        return catalogEntryKey;
    }

}
