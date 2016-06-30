/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 7, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.triggerservice.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.oscm.dataservice.local.DataService;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.intf.TriggerService;
import org.oscm.internal.types.exception.ExecutionTargetException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.types.exception.TriggerProcessStatusException;
import org.oscm.internal.vo.VOLocalizedText;
import org.oscm.rest.external.exceptions.AuthorizationException;
import org.oscm.rest.external.exceptions.ConflictException;
import org.oscm.rest.external.exceptions.NotFoundException;
import org.oscm.rest.trigger.interfaces.TriggerProcessRest;
import org.oscm.rest.trigger.interfaces.TriggerProcessRestService;

/**
 * Adapter service bean for REST service trigger process
 * 
 * @author miethaner
 */
@Stateless
@Remote(TriggerProcessRestService.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class TriggerProcessRestServiceBean implements TriggerProcessRestService {

    @EJB(beanInterface = DataService.class)
    protected DataService dm;

    @EJB(beanInterface = TriggerService.class)
    private TriggerService service;

    @Override
    public void approve(TriggerProcessRest process) throws NotFoundException,
            AuthorizationException, ConflictException {

        if (process.getId() == null) {
            throw new NotFoundException(new NullPointerException());
        }

        try {
            service.approveAction(process.getId().longValue());
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e);
        } catch (OperationNotPermittedException e) {
            throw new AuthorizationException(e);
        } catch (TriggerProcessStatusException e) {
            throw new ConflictException(e);
        } catch (ExecutionTargetException e) {
            throw new ConflictException(e);
        }

    }

    @Override
    public void reject(TriggerProcessRest process) throws NotFoundException,
            AuthorizationException, ConflictException {

        if (process.getId() == null) {
            throw new NotFoundException(new NullPointerException());
        }

        List<VOLocalizedText> reason = new ArrayList<VOLocalizedText>();
        reason.add(new VOLocalizedText(Locale.ENGLISH.getLanguage(), process
                .getComment()));

        try {
            service.rejectAction(process.getId().longValue(), reason);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e);
        } catch (OperationNotPermittedException e) {
            throw new AuthorizationException(e);
        } catch (TriggerProcessStatusException e) {
            throw new ConflictException(e);
        }

    }

    @Override
    public void cancel(TriggerProcessRest process) throws NotFoundException,
            AuthorizationException, ConflictException {

        if (process.getId() == null) {
            throw new NotFoundException(new NullPointerException());
        }

        List<Long> keys = new ArrayList<Long>();
        keys.add(process.getId());

        List<VOLocalizedText> reason = new ArrayList<VOLocalizedText>();
        reason.add(new VOLocalizedText(Locale.ENGLISH.getLanguage(), process
                .getComment()));

        try {
            service.cancelActions(keys, reason);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e);
        } catch (OperationNotPermittedException e) {
            throw new AuthorizationException(e);
        } catch (TriggerProcessStatusException e) {
            throw new ConflictException(e);
        }
    }

}
