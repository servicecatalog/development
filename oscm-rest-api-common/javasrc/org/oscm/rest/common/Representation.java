/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 12, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.util.UUID;

import javax.ws.rs.WebApplicationException;

/**
 * Base class for all representations
 * 
 * @author miethaner
 */
public abstract class Representation {

    private transient int version;
    private UUID id;

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
    public Representation(UUID id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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
