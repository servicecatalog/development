/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: May 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * Request filter for validating the requested version and comparing with
 * endpoint annotations (Since and Until).
 * 
 * @author miethaner
 */
@Provider
public class VersionFilter implements ContainerRequestFilter {

    public static final String PATTERN_VERSION = "v[0-9]+";
    public static final int OFFSET_VERSION = 1;

    @Context
    private ResourceInfo resourceInfo;

    public void setResourceInfo(ResourceInfo resourceInfo) {
        this.resourceInfo = resourceInfo;
    }

    @Override
    public void filter(ContainerRequestContext request)
            throws WebApplicationException {

        MultivaluedMap<String, String> params = request.getUriInfo()
                .getPathParameters();

        if (params.containsKey(CommonParams.PARAM_VERSION)
                && !params.get(CommonParams.PARAM_VERSION).isEmpty()) {

            String version = params.get(CommonParams.PARAM_VERSION).get(0);

            int vnr = validateVersion(version);

            Method method = resourceInfo.getResourceMethod();

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

            request.setProperty(CommonParams.PARAM_VERSION, new Integer(vnr));

        } else {
            throw WebException.notFound()
                    .message(CommonParams.ERROR_INVALID_VERSION).build();
        }
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

        int vnr = Integer.parseInt(
                version.substring(CommonParams.PATTERN_VERSION_OFFSET));

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
