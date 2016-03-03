/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 21.07.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.pagination;

/**
 * Generic pagination used to limit objects retrieved from database. Offset and
 * limit parameters are then transfered and set on
 * {@link javax.persistence.Query} using
 * {@link javax.persistence.Query#setFirstResult(int)} and
 * {@link javax.persistence.Query#setMaxResults(int)}
 */
public class Pagination {

    /**
     * Represents starting point from the beginning of the object pool.
     */
    private int offset;

    /**
     * Represents number of objects that should be retrieved.
     */
    private int limit;

    public Pagination() {
    }

    /**
     * @param offset
     *            - Start from given number
     * @param limit
     *            - Limited number of objects
     */
    public Pagination(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    /**
     * Returns how many elements should be skipped starting from the beginning
     * of object pool.
     * 
     * @return - The offset parameter
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets how many elements should be skipped starting from the beginning of
     * object pool.
     * 
     * @param offset
     *            - The offset parameter
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Retrieves the limit of items that should be retrieved from db.
     * 
     * @return - The limit parameter
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit of items that should be retrieved from db.
     * 
     * @param limit
     *            - The limit parameter
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }
}
