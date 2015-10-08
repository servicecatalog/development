/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2015??????????????????????????????????????????????????????????????????????????????????????
 * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
 *  Creation Date: 20.07.15 16:09
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
    void grantUserRoles(
            @WebParam(name = "user") VOUser user,
            @WebParam(name = "roles") List<UnitRoleType> roles,
            @WebParam(name = "organizationalUnit") VOOrganizationalUnit organizationalUnit)
            throws ObjectNotFoundException, OperationNotPermittedException;

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
    void revokeUserRoles(
            @WebParam(name = "user") VOUser user,
            @WebParam(name = "roles") List<UnitRoleType> roles,
            @WebParam(name = "organizationalUnit") VOOrganizationalUnit organizationalUnit)
            throws ObjectNotFoundException, OperationNotPermittedException;

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
            throws ObjectNotFoundException, OperationNotPermittedException,
            DeletionConstraintException, MailOperationException;
}
