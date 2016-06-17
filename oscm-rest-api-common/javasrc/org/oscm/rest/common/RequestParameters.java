/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

/**
 * Base class for InjectParams
 * 
 * @author miethaner
 */
public abstract class RequestParameters {

    private int version;

    @PathParam(CommonParams.PARAM_ID)
    private Long id;

    @HeaderParam(CommonParams.PARAM_MATCH)
    private String match;

    @HeaderParam(CommonParams.PARAM_NONE_MATCH)
    private String noneMatch;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getNoneMatch() {
        return noneMatch;
    }

    public void setNoneMatch(String noneMatch) {
        this.noneMatch = noneMatch;
    }

    /**
     * Validates the id string if it matches basic UUID format. Throws
     * NotFoundException if not valid.
     * 
     * @throws WebApplicationException
     */
    public void validateId() throws WebApplicationException {

        if (id == null) {
            throw WebException.notFound()
                    .message(CommonParams.ERROR_INVALID_ID).build();
        }
    }

    public void validateTag() throws WebApplicationException {

        if (match != null) {
            try {
                Long.parseLong(match);
            } catch (NumberFormatException e) {
                throw WebException.badRequest()
                        .message(CommonParams.ERROR_INVALID_TAG).build();
            }
        }

        if (noneMatch != null) {
            try {
                Long.parseLong(noneMatch);
            } catch (NumberFormatException e) {
                throw WebException.badRequest()
                        .message(CommonParams.ERROR_INVALID_TAG).build();
            }
        }
    }

    /**
     * Validates the content and format of the parameters. Throws
     * BadRequestException if not valid.
     * 
     * Subclasses also need also to validate fields of the base class (except
     * resource id) that they are using.
     * 
     * @throws WebApplicationException
     */
    public abstract void validateParameters() throws WebApplicationException;

    /**
     * Updates the parameters of the internal version to the current one.
     */
    public abstract void update();

}
