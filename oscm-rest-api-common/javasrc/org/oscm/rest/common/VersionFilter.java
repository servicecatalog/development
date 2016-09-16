/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.lang.annotation.Annotation;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * Request filter for validating the requested version and comparing with
 * endpoint annotations (Since and Until).
 * 
 * @author miethaner
 */
public class VersionFilter implements ContainerRequestFilter {

    private AbstractMethod method;
    private UriInfo uriInfo;

    /**
     * Creates a new version filter
     * 
     * @param method
     *            the called method
     */
    public VersionFilter(AbstractMethod method, UriInfo uriInfo) {
        this.method = method;
        this.uriInfo = uriInfo;
    }

    @Override
    public ContainerRequest filter(ContainerRequest request)
            throws WebApplicationException {

        MultivaluedMap<String, String> params = uriInfo.getPathParameters();

        if (params.containsKey(CommonParams.PARAM_VERSION)
                && !params.get(CommonParams.PARAM_VERSION).isEmpty()) {

            String version = params.get(CommonParams.PARAM_VERSION).get(0);

            int vnr = validateVersion(version);

            if (method.isAnnotationPresent(Since.class)) {

                Annotation annotation = method.getAnnotation(Since.class);
                Since since = (Since) annotation;

                if (vnr < since.value()) {
                    throw WebException.notFound()
                            .message(CommonParams.ERROR_METHOD_VERSION).build();
                }
            }
            if (method.isAnnotationPresent(Until.class)) {

                Annotation annotation = method.getAnnotation(Until.class);
                Until until = (Until) annotation;

                if (vnr >= until.value()) {
                    throw WebException.notFound()
                            .message(CommonParams.ERROR_METHOD_VERSION).build();
                }
            }

            request.getProperties().put(CommonParams.PARAM_VERSION,
                    new Integer(vnr));

        } else {
            throw WebException.notFound()
                    .message(CommonParams.ERROR_INVALID_VERSION).build();
        }

        return request;
    }

    /**
     * Validates the version string and compares it with the existing version
     * numbers. Throws a NotFoundException if not valid.
     * 
     * @param version
     *            the version string
     * @return the version as integer
     * @throws WebApplicationException
     */
    private int validateVersion(String version) throws WebApplicationException {

        if (version == null) {
            throw WebException.notFound()
                    .message(CommonParams.ERROR_INVALID_VERSION).build();
        }

        if (!version.matches(CommonParams.PATTERN_VERSION)) {
            throw WebException.notFound()
                    .message(CommonParams.ERROR_INVALID_VERSION).build();
        }

        int vnr = Integer.parseInt(version
                .substring(CommonParams.PATTERN_VERSION_OFFSET));

        boolean exists = false;
        for (int i : CommonParams.VERSIONS) {
            if (i == vnr) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            throw WebException.notFound()
                    .message(CommonParams.ERROR_INVALID_VERSION).build();
        }

        return vnr;
    }

}
