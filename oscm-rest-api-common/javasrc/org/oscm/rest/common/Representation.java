/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 12, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.WebApplicationException;

/**
 * Base class for all representations
 * 
 * @author miethaner
 */
public abstract class Representation {

    private transient Integer version;
    private String etag;
    private Long id;

    /**
     * Creates new representation
     */
    public Representation() {
    }

    /**
     * Creates new representation with resource id
     * 
     * @param id
     *            the resource id
     */
    public Representation(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTag() {
        return etag;
    }

    public void setTag(String tag) {
        this.etag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Validates the content and format of the fields to be legitimate. Throws
     * BadRequestException if not valid.
     * 
     * @throws WebApplicationException
     */
    public abstract void validateContent() throws WebApplicationException;

    /**
     * Updates the fields and format of the internal version to the current one
     */
    public abstract void update();

    /**
     * Converts the format and fields of the current version to the internal old
     * one
     */
    public abstract void convert();
}
