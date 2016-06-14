/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 13, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Class for common shared parameters
 * 
 * @author miethaner
 */
public class CommonParams {

    // glassfish realm
    public static final String REALM = "bss-realm";

    // global formating
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final String FORMAT_DATE = "yyyy-MM-dd'T'HH:mm:ssXXX";

    // all available versions
    public static final int VERSION_1 = 1;
    public static final int[] VERSIONS = { VERSION_1 };
    public static final int CURRENT_VERSION = VERSIONS[0];

    // parameter name
    public static final String PARAM_VERSION = "version";
    public static final String PARAM_ID = "id";
    public static final String PARAM_OFFSET = "offset";
    public static final String PARAM_LIMIT = "limit";

    // path params
    public static final String PATH_VERSION = "/{" + PARAM_VERSION + "}";
    public static final String PATH_ID = "/{" + PARAM_ID + "}";

    // pattern for version validation
    public static final String PATTERN_VERSION = "v[0-9]+";
    public static final int PATTERN_VERSION_OFFSET = 1;

    // patterns for validation
    public static final String PATTERN_STRING = "^[^<>%$§]{0,250}$";

    // http status values
    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_ACCEPTED = 202;
    public static final int STATUS_NO_CONTENT = 204;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_CONFLICT = 409;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
    public static final int STATUS_UNAVAILABLE = 503;

    // basic auth parameters
    public static final String HEADER_AUTH = "Authorization";
    public static final String BASIC_AUTH_PREFIX = "Basic ";
    public static final String BASIC_AUTH_SEPARATOR = ":";

    private CommonParams() {
    }
}
