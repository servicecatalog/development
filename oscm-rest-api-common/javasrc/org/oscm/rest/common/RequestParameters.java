/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.util.UUID;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

/**
 * Base class for InjectParams
 * 
 * @author miethaner
 */
public abstract class RequestParameters {

    private int version;

    @PathParam(CommonParams.PARAM_ID)
    private UUID id;

    @QueryParam(CommonParams.PARAM_OFFSET)
    private Integer offset;

    @QueryParam(CommonParams.PARAM_LIMIT)
    private Integer limit;

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

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Validates the id string if it matches basic UUID format. Throws
     * NotFoundException if not valid.
     * 
     * @throws WebApplicationException
     */
    public void validateResourceId() throws WebApplicationException {

        if (id == null) {
            throw WebException.notFound().build(); // TODO: add more info
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
