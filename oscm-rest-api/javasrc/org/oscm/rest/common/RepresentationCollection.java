/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.util.Collection;

import javax.ws.rs.BadRequestException;

/**
 * Generic representation class for collections of representation items
 * 
 * @author miethaner
 */
public class RepresentationCollection<T extends RepresentationWithVersion>
        extends RepresentationWithVersion {

    private Collection<T> items;

    public Collection<T> getItems() {
        return items;
    }

    public void setItems(Collection<T> items) {
        this.items = items;
    }

    /**
     * Creates a new representative collection
     */
    public RepresentationCollection() {
    }

    /**
     * Creates a new representative collection of items
     * 
     * @param items
     *            the representation items
     */
    public RepresentationCollection(Collection<T> items) {
        this.items = items;
    }

    @Override
    public void validateContent() throws BadRequestException {

        if (items != null) {
            for (T item : items) {
                item.validateContent();
            }
        }
    }

    @Override
    public void update(int version) {
        setVersion(version);

        for (T item : items) {
            item.update(version);
        }

        // nothing to update in version 1
    }

    @Override
    public void convert(int version) {
        setVersion(version);

        for (T item : items) {
            item.convert(version);
        }

        // nothing to convert in version 1
    }

}
