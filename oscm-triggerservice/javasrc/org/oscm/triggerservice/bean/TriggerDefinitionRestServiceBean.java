/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 7, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.triggerservice.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Organization;
import org.oscm.domobjects.TriggerDefinition;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.intf.TriggerDefinitionService;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.TriggerTargetType;
import org.oscm.internal.types.enumtypes.TriggerType;
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.DeletionConstraintException;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.types.exception.SaaSSystemException;
import org.oscm.internal.types.exception.TriggerDefinitionDataException;
import org.oscm.internal.types.exception.ValidationException;
import org.oscm.internal.types.exception.ValidationException.ReasonEnum;
import org.oscm.internal.vo.VOTriggerDefinition;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.rest.external.exceptions.AuthorizationException;
import org.oscm.rest.external.exceptions.BadDataException;
import org.oscm.rest.external.exceptions.ConflictException;
import org.oscm.rest.external.exceptions.DataException;
import org.oscm.rest.external.exceptions.NotFoundException;
import org.oscm.rest.trigger.interfaces.TriggerDefinitionRest;
import org.oscm.rest.trigger.interfaces.TriggerDefinitionRestService;
import org.oscm.triggerservice.assembler.TriggerDefinitionAssembler;
import org.oscm.types.enumtypes.LogMessageIdentifier;

/**
 * Adapter service bean for REST services trigger definition
 * 
 * @author miethaner
 */
@Stateless
@Remote(TriggerDefinitionRestService.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class TriggerDefinitionRestServiceBean implements
        TriggerDefinitionRestService {

    private final static Log4jLogger logger = LoggerFactory
            .getLogger(TriggerDefinitionRestServiceBean.class);

    private static TriggerType[] allowedTriggersForSupplier = {
            TriggerType.ACTIVATE_SERVICE, TriggerType.DEACTIVATE_SERVICE,
            TriggerType.REGISTER_CUSTOMER_FOR_SUPPLIER,
            TriggerType.SAVE_PAYMENT_CONFIGURATION,
            TriggerType.START_BILLING_RUN, TriggerType.SUBSCRIPTION_CREATION,
            TriggerType.SUBSCRIPTION_MODIFICATION,
            TriggerType.SUBSCRIPTION_TERMINATION, TriggerType.REGISTER_OWN_USER };

    private static TriggerType[] allowedTriggersForCustomer = {
            TriggerType.ADD_REVOKE_USER, TriggerType.MODIFY_SUBSCRIPTION,
            TriggerType.SUBSCRIBE_TO_SERVICE,
            TriggerType.UNSUBSCRIBE_FROM_SERVICE,
            TriggerType.UPGRADE_SUBSCRIPTION, TriggerType.START_BILLING_RUN,
            TriggerType.REGISTER_OWN_USER };

    @EJB(beanInterface = TriggerDefinitionService.class)
    private TriggerDefinitionService service;

    public void setService(TriggerDefinitionService service) {
        this.service = service;
    }

    @EJB(beanInterface = DataService.class)
    private DataService dm;

    public void setDataService(DataService dm) {
        this.dm = dm;
    }

    @RolesAllowed({ "ORGANIZATION_ADMIN", "PLATFORM_OPERATOR" })
    @Override
    public Long createDefinition(TriggerDefinitionRest definition)
            throws ConflictException, BadDataException {

        VOTriggerDefinition vo = tranferToVO(null, definition);

        Organization organization = getOwnOrganization();

        // check if trigger type is allowed for that organization
        if (vo.getType() == null) {
            throw new BadDataException(new NullPointerException());
        }

        if (!isAllowedTriggertype(vo.getType())) {
            throw new BadDataException(new ValidationException(
                    ReasonEnum.TRIGGER_TYPE_NOT_ALLOWED, vo.getType().name(),
                    null));
        }

        if (vo.isSuspendProcess()
                && organization.getSuspendingTriggerDefinition(vo.getType()) != null) {
            TriggerDefinitionDataException e = new TriggerDefinitionDataException(
                    "A suspending trigger definition does already exists for the type '"
                            + vo.getType() + "'");
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    e,
                    LogMessageIdentifier.ERROR_SUSPENDING_TRIGGER_ALREADY_EXISTS_FOR_TYPE,
                    String.valueOf(vo.getType()));
            throw new ConflictException(e);
        }

        try {
            // default name
            if (vo.getName() == null) {
                vo.setName(vo.getType().name());
            }
            TriggerDefinition triggerDefinition = TriggerDefinitionAssembler
                    .toTriggerDefinition(vo);
            triggerDefinition.setOrganization(organization);

            dm.persist(triggerDefinition);
            dm.flush();

            return new Long(triggerDefinition.getKey());
        } catch (ValidationException e) {
            throw new BadDataException(e);
        } catch (NonUniqueBusinessKeyException e) {
            // should not happen as the saved object doesn't have a business
            // key
            SaaSSystemException se = new SaaSSystemException(
                    "Trigger has no business key.", e);
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    se,
                    LogMessageIdentifier.ERROR_TRIGGER_DEFINITION_HAS_NO_BUSINESS_KEY);
            throw new ConflictException(se);
        }
    }

    @Override
    public void deleteDefinition(Long id) throws ConflictException,
            AuthorizationException, NotFoundException {

        try {
            service.deleteTriggerDefinition(id.longValue());
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e);
        } catch (DeletionConstraintException e) {
            throw new ConflictException(e);
        } catch (OperationNotPermittedException e) {
            throw new AuthorizationException(e);
        }

    }

    @Override
    public void updateDefinition(TriggerDefinitionRest definition)
            throws ConflictException, AuthorizationException,
            NotFoundException, BadDataException, DataException {

        if (definition.getId() == null) {
            throw new NotFoundException(new NullPointerException());
        }

        TriggerDefinition domObj;
        try {
            domObj = dm.getReference(TriggerDefinition.class, definition
                    .getId().longValue());
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e);
        }

        try {
            service.updateTriggerDefinition(tranferToVO(domObj, definition));
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e);
        } catch (ValidationException e) {
            throw new BadDataException(e);
        } catch (ConcurrentModificationException e) {
            throw new ConflictException(e);
        } catch (TriggerDefinitionDataException e) {
            throw new ConflictException(e);
        } catch (OperationNotPermittedException e) {
            throw new AuthorizationException(e);
        }
    }

    @RolesAllowed({ "ORGANIZATION_ADMIN", "PLATFORM_OPERATOR" })
    @Override
    public TriggerDefinitionRest getDefinition(Long id)
            throws AuthorizationException, NotFoundException {

        if (id == null) {
            throw new NotFoundException(new NullPointerException());
        }

        TriggerDefinition definition;
        try {
            definition = dm.getReference(TriggerDefinition.class,
                    id.longValue());
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e);
        }

        try {
            checkOrgAuthority(definition);
        } catch (OperationNotPermittedException e) {
            throw new AuthorizationException(e);
        }

        return TriggerDefinitionAssembler.toVOTriggerDefinition(definition);
    }

    @Override
    public Collection<TriggerDefinitionRest> getDefinitions() {
        return new ArrayList<TriggerDefinitionRest>(
                service.getTriggerDefinitions());
    }

    private void checkOrgAuthority(TriggerDefinition triggerDefinition)
            throws OperationNotPermittedException {
        if (getOwnOrganization().getKey() != triggerDefinition
                .getOrganization().getKey()) {
            OperationNotPermittedException ex = new OperationNotPermittedException(
                    "The client has no authority for the operation."
                            + triggerDefinition.getKey());
            logger.logInfo(Log4jLogger.SYSTEM_LOG,
                    LogMessageIdentifier.ERROR_NO_AUTHORITY_TO_APPROVE,
                    ex.getMessage());
            throw ex;
        }
    }

    private Organization getOwnOrganization() {
        Organization orgOfCurrentUser = dm.getCurrentUser().getOrganization();
        return orgOfCurrentUser;

    }

    private boolean isAllowedTriggertype(TriggerType triggerType) {
        Organization org = dm.getCurrentUser().getOrganization();
        Set<OrganizationRoleType> roles = org.getGrantedRoleTypes();
        for (OrganizationRoleType role : roles) {
            Set<TriggerType> allowedRoles = getTriggerTypesForRole(role);
            if (allowedRoles.contains(triggerType)) {
                return true;
            }
        }
        return false;
    }

    private Set<TriggerType> getTriggerTypesForRole(OrganizationRoleType role) {
        Set<TriggerType> triggerTypes = new HashSet<TriggerType>();

        switch (role) {
        case SUPPLIER:
            triggerTypes.addAll(Arrays.asList(allowedTriggersForSupplier));
            break;
        case CUSTOMER:
            triggerTypes.addAll(Arrays.asList(allowedTriggersForCustomer));
            break;
        default:
            triggerTypes = Collections.emptySet();
        }
        return triggerTypes;
    }

    private VOTriggerDefinition tranferToVO(TriggerDefinition domObj,
            TriggerDefinitionRest definition) {

        VOTriggerDefinition vo;

        if (domObj == null) {
            vo = new VOTriggerDefinition();
        } else {
            vo = TriggerDefinitionAssembler.toVOTriggerDefinition(domObj);
        }

        vo.setId(definition.getId());

        if (definition.getTag() != null && !"*".equals(definition.getTag())) {
            vo.setVersion(Integer.parseInt(definition.getTag()));
        }

        if (definition.getDescription() != null) {
            vo.setName(definition.getDescription());
        }

        if (definition.getServiceType() != null) {
            vo.setTargetType(TriggerTargetType.valueOf(definition
                    .getServiceType()));
        }

        if (definition.getTargetURL() != null) {
            vo.setTarget(definition.getTargetURL());
        }

        if (definition.isSuspending() != null) {
            vo.setSuspendProcess(definition.isSuspending().booleanValue());
        }

        if (definition.getAction() != null) {
            vo.setType(TriggerType.valueOf(definition.getAction()));
        }

        return vo;
    }

}
