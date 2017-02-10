/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * This object represent a link to a operation with certain preset parameters.
 * 
 * @author hoffmann
 */
public class QuickLink {

    private final String title;

    private final String operation;

    private final Map<String, String> parameters = new HashMap<String, String>();

    QuickLink(final String title, final String operation,
            Map<String, String> parameters) {
        this.title = title;
        this.operation = operation;
        this.parameters.putAll(parameters);
    }

    public void addParameter(final String name, final String value) {
        parameters.put(name, value);
    }

    public String getTitle() {
        return title;
    }

    public String getOperation() {
        return operation;
    }

    public String getQueryString() throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append("operation=").append(URLEncoder.encode(operation, "UTF-8"));
        for (final Map.Entry<String, String> param : parameters.entrySet()) {
            sb.append("&param_").append(param.getKey()).append("=");
            sb.append(URLEncoder.encode(param.getValue(), "UTF-8"));
        }
        return sb.toString();
    }
}
