/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PathParam;

/**
 * Super class for BeanParams
 * 
 * @author miethaner
 */
public abstract class RequestParameters {

    // parameter names
    private static final String PARAM_ID = "id";

    // pattern for input validation
    private static final String PATTERN_ID = "[0-9a-fA-F-]{36}";

    @PathParam(PARAM_ID)
    private String id;

    public String getID() {
        return id;
    }

    /**
     * Validates the id string if it matches basic UUID format. Throws
     * NotFoundException if not valid.
     * 
     * @throws NotFoundException
     */
    public void validateResourceId() throws NotFoundException {

        if (id == null) {
            throw WebException.notFound().build(); // TODO: add more info
        }

        if (!id.matches(PATTERN_ID)) {
            throw WebException.notFound().build(); // TODO: add more info
        }
    }

    /**
     * Validates the content and format of the parameters. Throws
     * BadRequestException if not valid.
     * 
     * @throws BadRequestException
     */
    public abstract void validateParameters() throws BadRequestException;

    /**
     * Updates the parameters of the given old version to the current one.
     * 
     * @param version
     *            the old version
     */
    public abstract void update(int version);

}
