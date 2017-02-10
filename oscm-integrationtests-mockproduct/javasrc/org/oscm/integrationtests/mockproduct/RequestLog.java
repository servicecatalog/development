/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct;

import java.util.ArrayList;
import java.util.List;

import org.oscm.integrationtests.mockproduct.RequestLogEntry.RequestDirection;

/**
 * Chronological list of requests.
 * 
 * @author hoffmann
 */
public class RequestLog {

    private final List<RequestLogEntry> entries = new ArrayList<RequestLogEntry>();

    public RequestLogEntry createEntry(String title, RequestDirection direction) {
        final RequestLogEntry entry = new RequestLogEntry(System
                .currentTimeMillis(), title, direction);
        entries.add(entry);
        return entry;
    }

    public List<RequestLogEntry> getEntries() {
        return new ArrayList<RequestLogEntry>(entries);
    }

    public void clear() {
        entries.clear();
    }

}
