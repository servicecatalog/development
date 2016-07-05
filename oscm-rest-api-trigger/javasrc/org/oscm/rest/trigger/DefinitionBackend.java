/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 4, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.WebApplicationException;

import org.oscm.internal.intf.TriggerDefinitionService;
import org.oscm.internal.types.enumtypes.TriggerTargetType;
import org.oscm.internal.types.enumtypes.TriggerType;
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.DeletionConstraintException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.types.exception.TriggerDefinitionDataException;
import org.oscm.internal.types.exception.ValidationException;
import org.oscm.internal.vo.VOTriggerDefinition;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.common.RestBackend.Get;
import org.oscm.rest.common.WebException;
import org.oscm.rest.trigger.data.DefinitionRepresentation;

/**
 * Backend class for the trigger definition resource.
 * 
 * @author miethaner
 */
@Stateless
public class DefinitionBackend {

    @EJB
    private TriggerDefinitionService service;

    public void setService(TriggerDefinitionService service) {
        this.service = service;
    }

    public RestBackend.Get<DefinitionRepresentation, TriggerParameters> getItem()
            throws WebApplicationException {

        return new RestBackend.Get<DefinitionRepresentation, TriggerParameters>() {

            @Override
            public DefinitionRepresentation get(TriggerParameters params)
                    throws WebApplicationException {

                VOTriggerDefinition definition;
                try {
                    definition = service.getTriggerDefinition(params.getId());
                } catch (ObjectNotFoundException e) {
                    throw WebException.notFound().message(e.getMessage())
                            .build();
                } catch (OperationNotPermittedException e) {
                    throw WebException.forbidden().message(e.getMessage())
                            .build();
                } catch (Exception e) {
                    if (e instanceof javax.ejb.EJBAccessException) {
                        throw WebException.forbidden()
                                .message(CommonParams.ERROR_NOT_AUTHORIZED)
                                .build();
                    } else {
                        throw e;
                    }
                }

                return new DefinitionRepresentation(definition);
            }
        };
    }

    public Get<RepresentationCollection<DefinitionRepresentation>, TriggerParameters> getCollection()
            throws WebApplicationException {

        return new RestBackend.Get<RepresentationCollection<DefinitionRepresentation>, TriggerParameters>() {
            @Override
            public RepresentationCollection<DefinitionRepresentation> get(
                    TriggerParameters params) throws WebApplicationException {

                Collection<VOTriggerDefinition> definitions;
                try {
                    definitions = service.getTriggerDefinitions();
                } catch (Exception e) {
                    if (e instanceof javax.ejb.EJBAccessException) {
                        throw WebException.forbidden()
                                .message(CommonParams.ERROR_NOT_AUTHORIZED)
                                .build();
                    } else {
                        throw e;
                    }
                }

                Collection<DefinitionRepresentation> representationList = new ArrayList<DefinitionRepresentation>();

                for (VOTriggerDefinition d : definitions) {
                    representationList.add(new DefinitionRepresentation(d));
                }

                return new RepresentationCollection<DefinitionRepresentation>(
                        representationList);
            }
        };
    }

    public RestBackend.Post<DefinitionRepresentation, TriggerParameters> postCollection()
            throws WebApplicationException {

        return new RestBackend.Post<DefinitionRepresentation, TriggerParameters>() {

            @Override
            public Object post(DefinitionRepresentation content,
                    TriggerParameters params) throws WebApplicationException {

                try {
                    return service
                            .createTriggerDefinition(tranferToVO(content));
                } catch (TriggerDefinitionDataException e) {
                    throw WebException.conflict().message(e.getMessage())
                            .build();
                } catch (ValidationException e) {
                    throw WebException.badRequest().message(e.getMessage())
                            .build();
                } catch (Exception e) {
                    if (e instanceof javax.ejb.EJBAccessException) {
                        throw WebException.forbidden()
                                .message(CommonParams.ERROR_NOT_AUTHORIZED)
                                .build();
                    } else {
                        throw e;
                    }
                }
            }
        };
    }

    public RestBackend.Put<DefinitionRepresentation, TriggerParameters> putItem()
            throws WebApplicationException {

        return new RestBackend.Put<DefinitionRepresentation, TriggerParameters>() {

            @Override
            public void put(DefinitionRepresentation content,
                    TriggerParameters params) throws WebApplicationException {

                try {

                    VOTriggerDefinition definition = tranferToVO(content);

                    if (content.getTag() == null) {
                        definition.setVersion(service.getTriggerDefinition(
                                params.getId()).getVersion());
                    }

                    service.updateTriggerDefinition(definition);
                } catch (ObjectNotFoundException e) {
                    throw WebException.notFound().message(e.getMessage())
                            .build();
                } catch (ValidationException e) {
                    throw WebException.badRequest().message(e.getMessage())
                            .build();
                } catch (ConcurrentModificationException e) {
                    throw WebException.conflict().message(e.getMessage())
                            .build();
                } catch (TriggerDefinitionDataException e) {
                    throw WebException.conflict().message(e.getMessage())
                            .build();
                } catch (OperationNotPermittedException e) {
                    throw WebException.forbidden().message(e.getMessage())
                            .build();
                } catch (Exception e) {
                    if (e instanceof javax.ejb.EJBAccessException) {
                        throw WebException.forbidden()
                                .message(CommonParams.ERROR_NOT_AUTHORIZED)
                                .build();
                    } else {
                        throw e;
                    }
                }

            }
        };
    }

    public RestBackend.Delete<TriggerParameters> deleteItem()
            throws WebApplicationException {

        return new RestBackend.Delete<TriggerParameters>() {

            @Override
            public void delete(TriggerParameters params)
                    throws WebApplicationException {

                try {
                    service.deleteTriggerDefinition(params.getId().longValue());
                } catch (ObjectNotFoundException e) {
                    throw WebException.notFound().message(e.getMessage())
                            .build();
                } catch (DeletionConstraintException e) {
                    throw WebException.conflict().message(e.getMessage())
                            .build();
                } catch (OperationNotPermittedException e) {
                    throw WebException.forbidden().message(e.getMessage())
                            .build();
                } catch (Exception e) {
                    if (e instanceof javax.ejb.EJBAccessException) {
                        throw WebException.forbidden()
                                .message(CommonParams.ERROR_NOT_AUTHORIZED)
                                .build();
                    } else {
                        throw e;
                    }
                }

            }
        };
    }

    private VOTriggerDefinition tranferToVO(DefinitionRepresentation rep) {
        VOTriggerDefinition definition = new VOTriggerDefinition();

        if (rep.getId() != null) {
            definition.setKey(rep.getId().longValue());
        }

        if (rep.getTag() != null) {
            definition.setVersion(Integer.parseInt(rep.getTag()));
        }

        if (rep.getDescription() != null) {
            definition.setName(rep.getDescription());
        }

        if (rep.getAction() != null) {
            definition.setType(TriggerType.valueOf(rep.getAction()));
        }

        if (rep.getType() != null) {
            definition.setTargetType(TriggerTargetType.valueOf(rep.getType()));
        }

        if (rep.isSuspending() != null) {
            definition.setSuspendProcess(rep.isSuspending().booleanValue());
        }

        if (rep.getTargetURL() != null) {
            definition.setTarget(rep.getTargetURL());
        }

        return definition;
    }
}
