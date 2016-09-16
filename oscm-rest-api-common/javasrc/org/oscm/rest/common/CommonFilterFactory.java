/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 12, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;

/**
 * Factory class for all common filters for the endpoints
 * 
 * @author miethaner
 */
@Provider
public class CommonFilterFactory implements ResourceFilterFactory {

    /**
     * Wrapper class for the actual filters
     * 
     * @author miethaner
     */
    private class CommonResourceFilter implements ResourceFilter {

        private ContainerRequestFilter requestFilter;
        private ContainerResponseFilter responseFilter;

        public CommonResourceFilter(ContainerRequestFilter requestFilter,
                ContainerResponseFilter responseFilter) {
            this.requestFilter = requestFilter;
            this.responseFilter = responseFilter;
        }

        @Override
        public ContainerRequestFilter getRequestFilter() {
            return requestFilter;
        }

        @Override
        public ContainerResponseFilter getResponseFilter() {
            return responseFilter;
        }
    }

    @Context
    private UriInfo uriInfo;

    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod method) {

        List<ResourceFilter> filter = new ArrayList<ResourceFilter>();
        filter.add(new CommonResourceFilter(new VersionFilter(method, uriInfo),
                null));
        filter.add(new CommonResourceFilter(new SecurityFilter(), null));

        return filter;
    }

}
