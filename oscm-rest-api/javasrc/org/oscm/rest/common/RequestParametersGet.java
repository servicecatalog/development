/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.QueryParam;

/**
 * BeanParam class for get requests.
 * 
 * @author miethaner
 */
public class RequestParametersGet extends RequestParameters {

    private static final String PARAM_LIMIT = "limit";
    private static final String PARAM_OFFSET = "offset";

    @QueryParam(PARAM_LIMIT)
    private Integer limit;

    @QueryParam(PARAM_OFFSET)
    private Integer offset;

    public Integer getLimit() {
        return limit;
    }

    public Integer getOffset() {
        return offset;
    }

    @Override
    public void validateParameters() throws BadRequestException {
        if (limit != null) {
            if (limit.intValue() < 0) {
                throw WebException.badRequest().build(); // TODO add more info
            }
        }

        if (offset != null) {
            if (offset.intValue() < 0) {
                throw WebException.badRequest().build(); // TODO add more info
            }
        }
    }

    @Override
    public void update(int version) {
        // nothing to update in version 1
    }
}
