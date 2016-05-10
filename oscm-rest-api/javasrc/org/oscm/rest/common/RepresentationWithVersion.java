/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.BadRequestException;

/**
 * Super class for representational objects with versions
 * 
 * @author miethaner
 */
public abstract class RepresentationWithVersion {

    private transient int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Validates the content and format of the fields to be legitimate. Throws
     * BadRequestException if not valid.
     * 
     * @throws BadRequestException
     */
    public abstract void validateContent() throws BadRequestException;

    /**
     * Updates the fields and format of the given old version to the current one
     * 
     * @param version
     *            the old version
     */
    public abstract void update(int version);

    /**
     * Converts the format and fields of the current version to the given old
     * one
     * 
     * @param version
     *            the target version
     */
    public abstract void convert(int version);
}
