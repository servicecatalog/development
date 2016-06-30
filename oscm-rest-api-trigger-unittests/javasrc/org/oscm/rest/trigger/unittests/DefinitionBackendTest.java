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
import java.util.Collection;

import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.rest.common.Representation;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.trigger.DefinitionBackend;
import org.oscm.rest.trigger.TriggerParameters;
import org.oscm.rest.trigger.data.DefinitionRepresentation;
import org.oscm.rest.trigger.interfaces.TriggerDefinitionRest;
import org.oscm.rest.trigger.interfaces.TriggerDefinitionRestService;

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

        DefinitionRepresentation trigger = new DefinitionRepresentation();
        trigger.setId(id);
        trigger.setOwner(new DefinitionRepresentation.Owner());

        TriggerDefinitionRestService service = Mockito
                .mock(TriggerDefinitionRestService.class);

        Mockito.when(service.getDefinition(params.getId())).thenReturn(trigger);

        DefinitionBackend backend = new DefinitionBackend();
        backend.setService(service);
        Representation result = backend.getItem().get(params);

        assertEquals(id, result.getId());
        Mockito.verify(service).getDefinition(params.getId());
    }

    @Test
    public void testGetCollection() throws Exception {

        Long id = new Long(1L);

        TriggerParameters params = new TriggerParameters();
        params.setId(id);

        DefinitionRepresentation trigger = new DefinitionRepresentation();
        trigger.setId(id);
        trigger.setOwner(new DefinitionRepresentation.Owner());

        Collection<TriggerDefinitionRest> col = new ArrayList<TriggerDefinitionRest>();
        col.add(trigger);

        TriggerDefinitionRestService service = Mockito
                .mock(TriggerDefinitionRestService.class);

        Mockito.when(service.getDefinitions()).thenReturn(col);

        DefinitionBackend backend = new DefinitionBackend();
        backend.setService(service);
        RepresentationCollection<DefinitionRepresentation> result = backend
                .getCollection().get(params);

        assertEquals(id,
                result.getItems().toArray(new DefinitionRepresentation[] {})[0]
                        .getId());
        Mockito.verify(service).getDefinitions();
    }

    @Test
    public void testPostCollection() throws Exception {

        Long id = new Long(1L);

        TriggerParameters params = new TriggerParameters();
        params.setId(id);

        DefinitionRepresentation trigger = new DefinitionRepresentation();
        trigger.setId(id);
        trigger.setOwner(new DefinitionRepresentation.Owner());

        TriggerDefinitionRestService service = Mockito
                .mock(TriggerDefinitionRestService.class);

        Mockito.when(service.createDefinition(trigger)).thenReturn(id);

        DefinitionBackend backend = new DefinitionBackend();
        backend.setService(service);
        Object result = backend.postCollection().post(trigger, params);

        assertEquals(id.toString(), result.toString());
        Mockito.verify(service).createDefinition(trigger);
    }

    @Test
    public void testPutItem() throws Exception {

        Long id = new Long(1L);

        TriggerParameters params = new TriggerParameters();
        params.setId(id);

        DefinitionRepresentation trigger = new DefinitionRepresentation();
        trigger.setId(id);
        trigger.setOwner(new DefinitionRepresentation.Owner());

        TriggerDefinitionRestService service = Mockito
                .mock(TriggerDefinitionRestService.class);

        DefinitionBackend backend = new DefinitionBackend();
        backend.setService(service);
        backend.putItem().put(trigger, params);

        Mockito.verify(service).updateDefinition(trigger);
    }

    @Test
    public void testDeleteItem() throws Exception {

        Long id = new Long(1L);

        TriggerParameters params = new TriggerParameters();
        params.setId(id);

        TriggerDefinitionRestService service = Mockito
                .mock(TriggerDefinitionRestService.class);

        DefinitionBackend backend = new DefinitionBackend();
        backend.setService(service);
        backend.deleteItem().delete(params);

        Mockito.verify(service).deleteDefinition(id);
    }
}
