/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Apr 28, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.RestBackend;
import org.oscm.rest.common.WebException;
import org.oscm.rest.external.exceptions.AuthorizationException;
import org.oscm.rest.external.exceptions.ConflictException;
import org.oscm.rest.external.exceptions.DataException;
import org.oscm.rest.external.exceptions.NotFoundException;
import org.oscm.rest.trigger.data.ProcessRepresentation;
import org.oscm.rest.trigger.interfaces.TriggerProcessRestService;

/**
 * Backend class for the trigger process resource.
 * 
 * @author miethaner
 */
@Stateless
public class ProcessBackend {

    @EJB
    private TriggerProcessRestService service;

    public void setService(TriggerProcessRestService service) {
        this.service = service;
    }

    public RestBackend.Put<ProcessRepresentation, TriggerParameters> putApprove()
            throws WebApplicationException {

        return new RestBackend.Put<ProcessRepresentation, TriggerParameters>() {

            @Override
            public void put(ProcessRepresentation content,
                    TriggerParameters params) throws WebApplicationException {

                try {
                    service.approve(content);
                } catch (NotFoundException e) {
                    throw WebException.notFound().build();
                    // TODO add more info
                } catch (AuthorizationException e) {
                    throw WebException.forbidden().build();
                    // TODO add more info
                } catch (DataException e) {
                    throw WebException.internalServerError().build();
                    // TODO add more info
                } catch (ConflictException e) {
                    throw WebException.conflict().build();
                    // TODO add more info
                }
            }
        };
    }

    public RestBackend.Put<ProcessRepresentation, TriggerParameters> putReject()
            throws WebApplicationException {

        return new RestBackend.Put<ProcessRepresentation, TriggerParameters>() {

            @Override
            public void put(ProcessRepresentation content,
                    TriggerParameters params) throws WebApplicationException {

                try {
                    service.reject(content);
                } catch (NotFoundException e) {
                    throw WebException.notFound().build();
                    // TODO add more info
                } catch (AuthorizationException e) {
                    throw WebException.forbidden().build();
                    // TODO add more info
                } catch (DataException e) {
                    throw WebException.internalServerError().build();
                    // TODO add more info
                }
            }
        };
    }

    public RestBackend.Put<ProcessRepresentation, TriggerParameters> putCancel()
            throws WebApplicationException {

        return new RestBackend.Put<ProcessRepresentation, TriggerParameters>() {

            @Override
            public void put(ProcessRepresentation content,
                    TriggerParameters params) throws WebApplicationException {

                try {
                    service.cancel(content);
                } catch (NotFoundException e) {
                    throw WebException.notFound().build();
                    // TODO add more info
                } catch (AuthorizationException e) {
                    throw WebException.forbidden().build();
                    // TODO add more info
                } catch (DataException e) {
                    throw WebException.internalServerError().build();
                    // TODO add more info
                }
            }
        };

    }

}
