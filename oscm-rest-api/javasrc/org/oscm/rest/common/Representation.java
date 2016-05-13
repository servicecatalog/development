/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 12, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;

/**
 * Super class for all representations
 * 
 * @author miethaner
 */
public abstract class Representation {

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
