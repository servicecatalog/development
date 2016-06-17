/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Apr 29, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Builder class for custom WebAppExceptions.
 * 
 * @author miethaner
 */
public class WebException {

    public static class ExceptionBuilder {

        /**
         * Class for exception message body
         * 
         * @author miethaner
         */
        @SuppressWarnings("unused")
        private static class ExceptionBody extends Representation {
            private int code;
            private Integer error;
            private String property;
            private String message;
            private String moreInfo;

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public Integer getError() {
                return error;
            }

            public void setError(Integer error) {
                this.error = error;
            }

            public String getProperty() {
                return property;
            }

            public void setProperty(String property) {
                this.property = property;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getMoreInfo() {
                return moreInfo;
            }

            public void setMoreInfo(String moreInfo) {
                this.moreInfo = moreInfo;
            }

            @Override
            public void validateContent() throws WebApplicationException {
            }

            @Override
            public void update() {
            }

            @Override
            public void convert() {
            }
        }

        private ExceptionBody body;

        /**
         * Creates new exception builder
         * 
         * @param code
         *            the http status code
         */
        private ExceptionBuilder(int code) {
            body = new ExceptionBody();
            body.setCode(code);
            body.setError(null);
            body.setProperty(null);
            body.setMessage(null);
            body.setMoreInfo(null);
        }

        /**
         * Sets the message format (api) version
         * 
         * @param version
         *            the format/api version
         * @return the exception builder
         */
        public ExceptionBuilder version(int version) {
            body.setVersion(new Integer(version));
            return this;
        }

        /**
         * Sets internal error code
         * 
         * @param error
         *            the internal error code
         * @return the exception builder
         */
        public ExceptionBuilder error(int error) {
            body.setError(new Integer(error));
            return this;
        }

        /**
         * Sets the affected property
         * 
         * @param property
         *            the affected property
         * @return the exception builder
         */
        public ExceptionBuilder property(String property) {
            body.setProperty(property);
            return this;
        }

        /**
         * Sets the internal error message
         * 
         * @param message
         *            the error message
         * @return the exception builder
         */
        public ExceptionBuilder message(String message) {
            body.setMessage(message);
            return this;
        }

        /**
         * Sets the "more info" field
         * 
         * @param moreInfo
         *            the additional info
         * @return the exception builder
         */
        public ExceptionBuilder moreInfo(String moreInfo) {
            body.setMoreInfo(moreInfo);
            return this;
        }

        /**
         * Build the corresponding exception with the JSON body
         * 
         * @return the built exception
         */
        public WebApplicationException build() {

            Response response = null;

            switch (body.code) {
            case CommonParams.STATUS_BAD_REQUEST:
                response = Response.status(CommonParams.STATUS_BAD_REQUEST)
                        .entity(body).type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
                break;
            case CommonParams.STATUS_UNAUTHORIZED:
                response = Response.status(CommonParams.STATUS_UNAUTHORIZED)
                        .entity(body).type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
                break;
            case CommonParams.STATUS_FORBIDDEN:
                response = Response.status(CommonParams.STATUS_FORBIDDEN)
                        .entity(body).type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
                break;
            case CommonParams.STATUS_NOT_FOUND:
                response = Response.status(CommonParams.STATUS_NOT_FOUND)
                        .entity(body).type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
                break;
            case CommonParams.STATUS_CONFLICT:
                response = Response.status(CommonParams.STATUS_CONFLICT)
                        .entity(body).type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
                break;
            case CommonParams.STATUS_INTERNAL_SERVER_ERROR:
                response = Response
                        .status(CommonParams.STATUS_INTERNAL_SERVER_ERROR)
                        .entity(body).type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
                break;
            case CommonParams.STATUS_UNAVAILABLE:
                response = Response.status(CommonParams.STATUS_UNAVAILABLE)
                        .entity(body).type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
                break;
            }

            return new WebApplicationException(response);
        }
    }

    /**
     * Creates a builder for a bad request
     * 
     * @return the exception builder
     */
    public static ExceptionBuilder badRequest() {
        return new ExceptionBuilder(CommonParams.STATUS_BAD_REQUEST);
    }

    /**
     * Creates a builder for a unauthorized request
     * 
     * @return the exception builder
     */
    public static ExceptionBuilder unauthorized() {
        return new ExceptionBuilder(CommonParams.STATUS_UNAUTHORIZED);
    }

    /**
     * Creates a builder for a forbidden request
     * 
     * @return the exception builder
     */
    public static ExceptionBuilder forbidden() {
        return new ExceptionBuilder(CommonParams.STATUS_FORBIDDEN);
    }

    /**
     * Creates a builder for a request to non existing resource
     * 
     * @return the exception builder
     */
    public static ExceptionBuilder notFound() {
        return new ExceptionBuilder(CommonParams.STATUS_NOT_FOUND);
    }

    /**
     * Creates a builder for a conflicted request
     * 
     * @return the exception builder
     */
    public static ExceptionBuilder conflict() {
        return new ExceptionBuilder(CommonParams.STATUS_CONFLICT);
    }

    /**
     * Creates a builder for an internal server error
     * 
     * @return the exception builder
     */
    public static ExceptionBuilder internalServerError() {
        return new ExceptionBuilder(CommonParams.STATUS_INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a builder for a unavailable service
     * 
     * @return the exception builder
     */
    public static ExceptionBuilder unavailable() {
        return new ExceptionBuilder(CommonParams.STATUS_UNAVAILABLE);
    }
}
