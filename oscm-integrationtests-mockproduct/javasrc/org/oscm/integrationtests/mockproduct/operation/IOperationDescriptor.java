/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;

/**
 * This interface describes and implements an outbound web service call.
 * 
 * @author hoffmann
 */
public interface IOperationDescriptor<T> {

    public String getName();

    public Class<T> getServiceType();

    public List<String> getParameters();

    public String getComment();

    public void call(T service, RequestLogEntry logEntry,
            Map<String, String> parameters) throws Exception;

}
