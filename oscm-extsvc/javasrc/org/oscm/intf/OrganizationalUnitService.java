/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *******************************************************************************/

package org.oscm.intf;

import java.util.List;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.oscm.pagination.Pagination;
import org.oscm.types.enumtypes.UnitRoleType;
import org.oscm.types.exceptions.DeletionConstraintException;
import org.oscm.types.exceptions.MailOperationException;
import org.oscm.types.exceptions.NonUniqueBusinessKeyException;
import org.oscm.types.exceptions.ObjectNotFoundException;
import org.oscm.types.exceptions.OperationNotPermittedException;
import org.oscm.vo.VOOrganizationalUnit;
import org.oscm.vo.VOService;
import org.oscm.vo.VOUser;

/**
 * Remote interface used for handling organization units.
 */
@Remote
@WebService(targetNamespace = "http://oscm.org/xsd")
public interface OrganizationalUnitService {

    /**
     * Grants user roles in unit. If user already has roles in unit, nothing
     * will happen.
     * 
     * Required role: administrator of an organization
     * 
     * @param user
     *            - User that roles should be assigned to
     * @param roles
     *            - List of roles to assign to user
     * @param organizationalUnit
     *            - Unit in which roles should be granted for given user.
     * @throws ObjectNotFoundException
     *             - if user is not found, or user is not assigned to given unit
     * @throws OperationNotPermittedException
     */
    @WebMethod(operationName = "grantUserRolesInUnit")
    @RequestWrapper(className = "grantUserRolesRequest")
    @ResponseWrapper(className = "grantUserRolesResponse")
    void grantUserRoles(@WebParam(name = "user") VOUser user,
            @WebParam(name = "roles") List<UnitRoleType> roles,
            @WebParam(name = "organizationalUnit") VOOrganizationalUnit organizationalUnit)
                    throws ObjectNotFoundException,
                    OperationNotPermittedException;

    /**
     * Revokes roles from user in unit. If user does not have roles, nothing
     * will happen.
     * 
     * Required role: administrator of an organization
     * 
     * @param user
     *            - user that roles should be revoked from
     * @param roles
     *            - List of roles to revoke from user
     * @param organizationalUnit
     *            - Unit from which roles should be revoked for given user.
     * @throws ObjectNotFoundException
     *             - if user is not found, or user is not assigned to given unit
     * @throws OperationNotPermittedException
     */
    @WebMethod(operationName = "revokeUserRolesInUnit")
    @RequestWrapper(className = "revokeUserRolesRequest")
    @ResponseWrapper(className = "revokeUserRolesResponse")
    void revokeUserRoles(@WebParam(name = "user") VOUser user,
            @WebParam(name = "roles") List<UnitRoleType> roles,
            @WebParam(name = "organizationalUnit") VOOrganizationalUnit organizationalUnit)
                    throws ObjectNotFoundException,
                    OperationNotPermittedException;

    /**
     * Returns organizational units existing in the organization of the calling
     * user.
     * 
     * Required role: administrator of an organization
     * 
     * @param pagination
     *            - Used to retrieve only given amount of units.
     * @return - All units if pagination is null, otherwise amount of units
     *         restricted by pagination parameter.
     */
    @WebMethod
    List<VOOrganizationalUnit> getOrganizationalUnits(
            @WebParam(name = "pagination") Pagination pagination);

    /**
     * Creates new organizational unit in organization of the calling user.
     * 
     * Required role: administrator of an organization
     * 
     * @param unitName
     *            - New unit name
     * @param description
     *            - New unit description
     * @param referenceId
     *            - New unit referenceId
     * @return - Created organizational unit object
     * @throws NonUniqueBusinessKeyException
     *             - thrown if unit with given unit name already exists
     */
    @WebMethod
    VOOrganizationalUnit createUnit(
            @WebParam(name = "unitName") String unitName,
            @WebParam(name = "description") String description,
            @WebParam(name = "referenceId") String referenceId)
                    throws NonUniqueBusinessKeyException;

    /**
     * Deletes organizational unit in organization of the calling user.
     * 
     * Required role: administrator of an organization
     * 
     * @param organizationalUnitName
     *            - Name of the unit that should be deleted
     * @throws ObjectNotFoundException
     *             - specified unit not found for the calling user's
     *             organization
     * @throws OperationNotPermittedException
     *             - the specified unit does not belong to the calling user's
     *             organization or there are subscriptions assigned to this unit
     */
    @WebMethod
    void deleteUnit(
            @WebParam(name = "organizationalUnitName") String organizationalUnitName)
                    throws ObjectNotFoundException,
                    OperationNotPermittedException, DeletionConstraintException,
                    MailOperationException;

    /**
     * Returns list of visible services for the specified organizational unit.
     *
     * Visible services are these which are visible for organization admin
     * and unit admin, however they are hidden from regular marketplace users.
     *
     * Returned services have status ACTIVE or SUSPENDED.
     *
     * @param unitId
     *            - Id of the unit for which we get the services
     * @param pagination
     *            - Sorting, filtering, paging details
     * @param marketplaceId
     *            - Id of the marketplace to which the services are registered
     *            - If left empty, takes default value of 0,0 (offset, limit)
     * @return - List of visible services
     *         - Empty list if the values of unitId or marketplaceId do not exists
     *           in the underlying database
     */
    @WebMethod
    List<VOService> getVisibleServices(@WebParam(name = "unitId") String unitId,
            @WebParam(name = "pagination") Pagination pagination,
            @WebParam(name = "marketplaceId") String marketplaceId);

    /**
     * Returns list of accessible services for the specified organizational unit.
     *
     * Accessible services are these which are visible for organization admin
     * and unit admin, and also for the regular marketplace users.
     * They are not hidden by either organization admin or unit admin.
     *
     * Returned services have status ACTIVE or SUSPENDED.
     *
     * @param unitId
     *            - Id of the unit for which we get the services
     * @param pagination
     *            - Sorting, filtering, paging details
     *            - If left empty, takes default value of 0,0 (offset, limit)
     * @param marketplaceId
     *            - Id of the marketplace to which the services are registered
     * @return - List of accessible services
     *         - Empty list if the values of unitId or marketplaceId do not exists
     *           in the underlying database
     */
    @WebMethod
    List<VOService> getAccessibleServices(
            @WebParam(name = "unitId") String unitId,
            @WebParam(name = "pagination") Pagination pagination,
            @WebParam(name = "marketplaceId") String marketplaceId);

    /**
     * Sets the services as visible for the administrators of the
     * specified organizational unit.
     *
     * Visible services are these which are visible for organization admin
     * and unit admin, however they are hidden from regular marketplace users.
     *
     * If any of the provided service IDs does not exist as invisible in the underlying
     * database, it is ignored.
     *
     * @param unitId
      *           - Id of the unit for which the services will be set
     *              as visible
     * @param visibleServices
     *            - Keys of the services which will be set as visible
     */
    @WebMethod
    void addVisibleServices(
            @WebParam(name = "unitId") String unitId,
            @WebParam(name = "services") List<String> visibleServices);

    /**
     * Sets the services as not visible for the administrators of the
     * specified organizational unit.
     *
     * Visible services are these which are visible for organization admin
     * and unit admin, however they are hidden from regular marketplace users.
     *
     * If any of the provided service IDs does not exist as visible in the underlying
     * database, it is ignored.
     *
     * @param unitId
     *           - Id of the unit for which the services will be set
     *             as not visible
     * @param visibleServices
     *           - Keys of the services which will be set as not visible
     */
    @WebMethod
    void revokeVisibleServices(
            @WebParam(name = "unitId") String unitId,
            @WebParam(name = "services") List<String> visibleServices);

    /**
     * Sets the services as accessible for the specified organizational unit users.
     *
     * Accessible services are these which are visible for organization admin
     * and unit admin, and also for the regular marketplace users.
     * They are not hidden by either organization admin or unit admin.
     *
     * If any of the provided service IDs does not exist as inaccessible in the underlying
     * database, it is ignored.
     *
     * @param unitId
     *           - Id of the unit for which the services will be set
     *             as not accessible
     * @param accessibleServices
     *           - Keys of the services which will be set as accessible
     */
    @WebMethod
    void addAccessibleServices(
            @WebParam(name = "unitId") String unitId,
            @WebParam(name = "services") List<String> accessibleServices);

    /**
     * Sets the services as not accessible for the specified organizational unit users.
     *
     * Accessible services are these which are visible for organization admin
     * and unit admin, and also for the regular marketplace users.
     * They are not hidden by either organization admin or unit admin.
     *
     * If any of the provided service IDs does not exist as accessible in the underlying
     * database, it is ignored.
     *
     * @param unitId
     *           - Id of the unit for which the services will be set
     *              as not visible
     * @param accessibleServices
     *           - Keys of the services which will be set as not accessible
     */
    @WebMethod
    void revokeAccessibleServices(
            @WebParam(name = "unitId") String unitId,
            @WebParam(name = "services") List<String> accessibleServices);

}
