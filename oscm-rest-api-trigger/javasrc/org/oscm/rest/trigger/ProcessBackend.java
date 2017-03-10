/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: Apr 28, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

/**
 * Backend class for the trigger process resource.
 * 
 * @author miethaner
 */
//@Stateless
public class ProcessBackend {
//
//    @EJB
//    private TriggerService service;
//
//    public void setService(TriggerService service) {
//        this.service = service;
//    }
//
//    public RestBackend.Put<ProcessRepresentation, TriggerParameters> putApprove()
//            throws WebApplicationException {
//
//        return new RestBackend.Put<ProcessRepresentation, TriggerParameters>() {
//
//            @Override
//            public boolean put(ProcessRepresentation content,
//                    TriggerParameters params) throws WebApplicationException {
//
//                try {
//                    service.approveAction(params.getId().longValue());
//                } catch (ObjectNotFoundException e) {
//                    throw WebException.notFound().message(e.getMessage())
//                            .build();
//                } catch (OperationNotPermittedException e) {
//                    throw WebException.forbidden().message(e.getMessage())
//                            .build();
//                } catch (TriggerProcessStatusException e) {
//                    throw WebException.conflict().message(e.getMessage())
//                            .build();
//                } catch (ExecutionTargetException e) {
//                    throw WebException.conflict().message(e.getMessage())
//                            .build();
//                } catch (Exception e) {
//                    if (e instanceof javax.ejb.EJBAccessException) {
//                        throw WebException.forbidden()
//                                .message(CommonParams.ERROR_NOT_AUTHORIZED)
//                                .build();
//                    } else {
//                        throw e;
//                    }
//                }
//                return true;
//            }
//        };
//    }
//
//    public RestBackend.Put<ProcessRepresentation, TriggerParameters> putReject()
//            throws WebApplicationException {
//
//        return new RestBackend.Put<ProcessRepresentation, TriggerParameters>() {
//
//            @Override
//            public boolean put(ProcessRepresentation content,
//                    TriggerParameters params) throws WebApplicationException {
//
//                try {
//                    List<VOLocalizedText> reason = new ArrayList<VOLocalizedText>();
//                    reason.add(new VOLocalizedText(
//                            Locale.ENGLISH.getLanguage(), content.getComment()));
//
//                    service.rejectAction(params.getId().longValue(), reason);
//                } catch (ObjectNotFoundException e) {
//                    throw WebException.notFound().message(e.getMessage())
//                            .build();
//                } catch (OperationNotPermittedException e) {
//                    throw WebException.forbidden().message(e.getMessage())
//                            .build();
//                } catch (TriggerProcessStatusException e) {
//                    throw WebException.conflict().message(e.getMessage())
//                            .build();
//                } catch (Exception e) {
//                    if (e instanceof javax.ejb.EJBAccessException) {
//                        throw WebException.forbidden()
//                                .message(CommonParams.ERROR_NOT_AUTHORIZED)
//                                .build();
//                    } else {
//                        throw e;
//                    }
//                }
//                return true;
//            }
//        };
//    }
//
//    public RestBackend.Put<ProcessRepresentation, TriggerParameters> putCancel()
//            throws WebApplicationException {
//
//        return new RestBackend.Put<ProcessRepresentation, TriggerParameters>() {
//
//            @Override
//            public boolean put(ProcessRepresentation content,
//                    TriggerParameters params) throws WebApplicationException {
//
//                try {
//                    List<VOLocalizedText> reason = new ArrayList<VOLocalizedText>();
//                    reason.add(new VOLocalizedText(
//                            Locale.ENGLISH.getLanguage(), content.getComment()));
//
//                    service.cancelActions(Arrays.asList(params.getId()), reason);
//                } catch (ObjectNotFoundException e) {
//                    throw WebException.notFound().message(e.getMessage())
//                            .build();
//                } catch (OperationNotPermittedException e) {
//                    throw WebException.forbidden().message(e.getMessage())
//                            .build();
//                } catch (TriggerProcessStatusException e) {
//                    throw WebException.conflict().message(e.getMessage())
//                            .build();
//                } catch (Exception e) {
//                    if (e instanceof javax.ejb.EJBAccessException) {
//                        throw WebException.forbidden()
//                                .message(CommonParams.ERROR_NOT_AUTHORIZED)
//                                .build();
//                    } else {
//                        throw e;
//                    }
//                }
//                return true;
//            }
//        };
//
//    }

}
