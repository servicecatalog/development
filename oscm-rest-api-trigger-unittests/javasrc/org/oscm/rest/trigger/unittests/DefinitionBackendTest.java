/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 10, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.unittests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.internal.intf.TriggerDefinitionService;
import org.oscm.internal.vo.VOOrganization;
import org.oscm.internal.vo.VOTriggerDefinition;
import org.oscm.rest.common.Representation;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.trigger.DefinitionBackend;
import org.oscm.rest.trigger.TriggerParameters;
import org.oscm.rest.trigger.data.DefinitionRepresentation;

/**
 * Unit test for TriggerBackend
 * 
 * @author miethaner
 */
public class DefinitionBackendTest {

    @Test
    public void testGetItem() throws Exception {

        Long id = new Long(1L);

        TriggerParameters params = new TriggerParameters();
        params.setId(id);

        VOTriggerDefinition trigger = new VOTriggerDefinition();
        trigger.setKey(id.longValue());
        trigger.setOrganization(new VOOrganization());

        TriggerDefinitionService service = Mockito
                .mock(TriggerDefinitionService.class);

        Mockito.when(service.getTriggerDefinition(params.getId())).thenReturn(
                trigger);

        DefinitionBackend backend = new DefinitionBackend();
        backend.setService(service);
        Representation result = backend.getItem().get(params);

        assertEquals(id, result.getId());
        Mockito.verify(service).getTriggerDefinition(params.getId());
    }

    @Test
    public void testGetCollection() throws Exception {

        Long id = new Long(1L);

        TriggerParameters params = new TriggerParameters();
        params.setId(id);

        VOTriggerDefinition trigger = new VOTriggerDefinition();
        trigger.setKey(id.longValue());
        trigger.setOrganization(new VOOrganization());

        List<VOTriggerDefinition> col = new ArrayList<VOTriggerDefinition>();
        col.add(trigger);

        TriggerDefinitionService service = Mockito
                .mock(TriggerDefinitionService.class);

        Mockito.when(service.getTriggerDefinitions()).thenReturn(col);

        DefinitionBackend backend = new DefinitionBackend();
        backend.setService(service);
        RepresentationCollection<DefinitionRepresentation> result = backend
                .getCollection().get(params);

        assertEquals(id,
                result.getItems().toArray(new DefinitionRepresentation[] {})[0]
                        .getId());
        Mockito.verify(service).getTriggerDefinitions();
    }

    @Test
    public void testPostCollection() throws Exception {

        Long id = new Long(1L);

        TriggerParameters params = new TriggerParameters();
        params.setId(id);

        DefinitionRepresentation trigger = new DefinitionRepresentation();
        trigger.setId(id);
        trigger.setOwner(new DefinitionRepresentation.Owner());

        VOTriggerDefinition definition = new VOTriggerDefinition();
        definition.setKey(id.longValue());
        definition.setOrganization(new VOOrganization());

        TriggerDefinitionService service = Mockito
                .mock(TriggerDefinitionService.class);

        Mockito.when(service.createTriggerDefinition(definition))
                .thenReturn(id);

        DefinitionBackend backend = new DefinitionBackend();
        backend.setService(service);
        backend.postCollection().post(trigger, params);
    }

    @Test
    public void testPutItem() throws Exception {

        Long id = new Long(1L);

        TriggerParameters params = new TriggerParameters();
        params.setId(id);

        DefinitionRepresentation trigger = new DefinitionRepresentation();
        trigger.setId(id);
        trigger.setTag("0");
        trigger.setOwner(new DefinitionRepresentation.Owner());

        VOTriggerDefinition definition = new VOTriggerDefinition();
        definition.setKey(id.longValue());
        definition.setOrganization(new VOOrganization());

        TriggerDefinitionService service = Mockito
                .mock(TriggerDefinitionService.class);

        DefinitionBackend backend = new DefinitionBackend();
        backend.setService(service);
        backend.putItem().put(trigger, params);

    }

    @Test
    public void testDeleteItem() throws Exception {

        Long id = new Long(1L);

        TriggerParameters params = new TriggerParameters();
        params.setId(id);

        TriggerDefinitionService service = Mockito
                .mock(TriggerDefinitionService.class);

        DefinitionBackend backend = new DefinitionBackend();
        backend.setService(service);
        backend.deleteItem().delete(params);

        Mockito.verify(service).deleteTriggerDefinition(id.longValue());
    }
}
