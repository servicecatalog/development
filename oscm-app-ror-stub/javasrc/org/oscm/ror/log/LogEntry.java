/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 30.01.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror.log;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

/**
 * @author kulle
 * 
 */
public class LogEntry {

    private String version = null;

    private String locale = null;

    private String action = null;

    private String url = null;

    private MultivaluedMap<String, String> parameters;

    public LogEntry(UriInfo uriInfo) {
        parameters = uriInfo.getQueryParameters();

        if (parameters.get("Version") != null
                && parameters.get("Version").size() > 0) {
            version = parameters.get("Version").get(0);
        }

        if (parameters.get("Locale") != null
                && parameters.get("Locale").size() > 0) {
            locale = parameters.get("Locale").get(0);
        }

        if (parameters.get("Action") != null
                && parameters.get("Action").size() > 0) {
            action = parameters.get("Action").get(0);
        }

        url = uriInfo.getRequestUri().toString();
    }

    public String getVersion() {
        return version;
    }

    public String getLocale() {
        return locale;
    }

    public String getAction() {
        return action;
    }

    public String getUrl() {
        return url;
    }

    public MultivaluedMap<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n====== LOG ENRTY: ====== \n");
        sb.append("URL:    " + url + "\n");
        sb.append("Version:" + version + "\n");
        sb.append("Locale: " + locale + "\n");
        sb.append("Action: " + action + "\n");
        sb.append("Parameters: " + parameters.toString());
        sb.append("\n======================== \n");
        return sb.toString();
    }
}
