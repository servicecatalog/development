/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 10, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.unittests;

import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.rest.trigger.ProcessBackend;
import org.oscm.rest.trigger.TriggerParameters;
import org.oscm.rest.trigger.data.ProcessRepresentation;
import org.oscm.rest.trigger.interfaces.TriggerProcessRestService;

/**
 * @author miethaner
 *
 */
public class ProcessBackendTest {

    @Test
    public void testPutApprove() throws Exception {

        TriggerParameters params = new TriggerParameters();
        ProcessRepresentation process = new ProcessRepresentation();

        TriggerProcessRestService service = Mockito
                .mock(TriggerProcessRestService.class);

        ProcessBackend backend = new ProcessBackend();
        backend.setService(service);
        backend.putApprove().put(process, params);

        Mockito.verify(service).approve(process);
    }

    @Test
    public void testPutReject() throws Exception {

        TriggerParameters params = new TriggerParameters();
        ProcessRepresentation process = new ProcessRepresentation();

        TriggerProcessRestService service = Mockito
                .mock(TriggerProcessRestService.class);

        ProcessBackend backend = new ProcessBackend();
        backend.setService(service);
        backend.putReject().put(process, params);

        Mockito.verify(service).reject(process);
    }

    @Test
    public void testPutCancel() throws Exception {

        TriggerParameters params = new TriggerParameters();
        ProcessRepresentation process = new ProcessRepresentation();

        TriggerProcessRestService service = Mockito
                .mock(TriggerProcessRestService.class);

        ProcessBackend backend = new ProcessBackend();
        backend.setService(service);
        backend.putCancel().put(process, params);

        Mockito.verify(service).cancel(process);
    }
}
