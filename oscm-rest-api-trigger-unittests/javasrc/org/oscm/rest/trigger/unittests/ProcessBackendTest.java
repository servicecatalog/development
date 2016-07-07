/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 10, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.unittests;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.internal.intf.TriggerService;
import org.oscm.internal.vo.VOLocalizedText;
import org.oscm.rest.trigger.ProcessBackend;
import org.oscm.rest.trigger.TriggerParameters;
import org.oscm.rest.trigger.data.ProcessRepresentation;

/**
 * @author miethaner
 *
 */
public class ProcessBackendTest {

    @Test
    public void testPutApprove() throws Exception {

        TriggerParameters params = new TriggerParameters();
        ProcessRepresentation process = new ProcessRepresentation();
        params.setId(new Long(1L));

        TriggerService service = Mockito.mock(TriggerService.class);

        ProcessBackend backend = new ProcessBackend();
        backend.setService(service);
        backend.putApprove().put(process, params);
    }

    @Test
    public void testPutReject() throws Exception {

        TriggerParameters params = new TriggerParameters();
        ProcessRepresentation process = new ProcessRepresentation();
        process.setComment("comment");
        params.setId(new Long(1L));

        TriggerService service = Mockito.mock(TriggerService.class);

        ProcessBackend backend = new ProcessBackend();
        backend.setService(service);
        backend.putReject().put(process, params);

        List<VOLocalizedText> reason = new ArrayList<VOLocalizedText>();
        reason.add(new VOLocalizedText(Locale.ENGLISH.getLanguage(), process
                .getComment()));
    }

    @Test
    public void testPutCancel() throws Exception {

        TriggerParameters params = new TriggerParameters();
        ProcessRepresentation process = new ProcessRepresentation();
        process.setComment("comment");
        params.setId(new Long(1L));

        TriggerService service = Mockito.mock(TriggerService.class);

        ProcessBackend backend = new ProcessBackend();
        backend.setService(service);
        backend.putCancel().put(process, params);

        List<VOLocalizedText> reason = new ArrayList<VOLocalizedText>();
        reason.add(new VOLocalizedText(Locale.ENGLISH.getLanguage(), process
                .getComment()));
    }
}
