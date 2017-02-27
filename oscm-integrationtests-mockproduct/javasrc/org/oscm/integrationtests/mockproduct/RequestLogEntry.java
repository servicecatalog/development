/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of a inbound or outbound request.
 * 
 * @author hoffmann
 */
public class RequestLogEntry {

    public enum RequestDirection {
        INBOUND, OUTBOUND;
    }

    private final long timestamp;

    private final String title;

    private final RequestDirection direction;

    private String host;

    private final Map<String, String> parameters = new LinkedHashMap<String, String>();

    private String result;

    private Exception exception;

    private final List<QuickLink> quicklinks = new ArrayList<QuickLink>();

    RequestLogEntry(long timestamp, String title, RequestDirection direction) {
        this.timestamp = timestamp;
        this.title = title;
        this.direction = direction;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public RequestDirection getDirection() {
        return direction;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void addParameter(final String name, final Object value) {
        parameters.put(name, toString(value));
    }

    public void setResult(Object result) {
        this.result = toString(result);
    }

    public String getResult() {
        return result;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public String getExceptionTrace() {
        final StringWriter buffer = new StringWriter();
        if (exception != null) {
            final PrintWriter pw = new PrintWriter(buffer);
            exception.printStackTrace(pw);
            pw.flush();
        }
        return buffer.toString();
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public QuickLink addQuickLink(final String title, final String operation) {
        final QuickLink l = new QuickLink(title, operation, parameters);
        quicklinks.add(l);
        return l;
    }

    public List<QuickLink> getQuickLinks() {
        return Collections.unmodifiableList(quicklinks);
    }

    private String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj.getClass().isEnum()) {
            return ((Enum<?>) obj).name();
        } else if (obj.getClass().getName().startsWith("org.oscm.")) {
            StringBuilder buf = new StringBuilder();
            Method methods[] = obj.getClass().getMethods();
            for (Method m : methods) {
                if ("getClass".equals(m.getName())
                        || m.getGenericParameterTypes().length > 0) {
                    continue;
                }
                String name = m.getName();
                Object value = "";
                try {
                    if (name.startsWith("get")) {
                        name = name.substring(3, 4).toLowerCase()
                                + name.substring(4);
                        value = m.invoke(obj, (Object[]) null);
                    } else if (name.startsWith("is")) {
                        name = name.substring(2, 3).toLowerCase()
                                + name.substring(3);
                        value = m.invoke(obj, (Object[]) null);
                    } else {
                        continue;
                    }
                    if (buf.length() > 0) {
                        buf.append(", ");
                    }
                    buf.append(name).append("=").append(toString(value));
                } catch (Exception e) {
                }
            }
            StringBuffer result = new StringBuffer();
            result.append(obj.getClass().getName()).append(": {").append(buf)
                    .append("}");
            return result.toString();
        }
        if (obj instanceof Collection<?>) {
            StringBuilder buf = new StringBuilder();
            for (Object o : (Collection<?>) obj) {
                if (buf.length() > 0) {
                    buf.append(", ");
                }
                buf.append(toString(o));
            }
            return buf.insert(0, "[").append("]").toString();
        }
        if (obj.getClass().isArray()) {
            final int len = Array.getLength(obj);
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < len; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(toString(Array.get(obj, i)));
            }
            return buf.insert(0, "[").append("]").toString();
        }
        return String.valueOf(obj);
    }

}
