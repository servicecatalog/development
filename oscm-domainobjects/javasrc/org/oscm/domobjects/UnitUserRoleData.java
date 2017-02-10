/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 24.07.2010                                                     
 *                                                                              
 *******************************************************************************/
package org.oscm.domobjects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.oscm.internal.types.enumtypes.UnitRoleType;

/**
 * Data container for UnitUserRole
 */
@Embeddable
public class UnitUserRoleData extends DomainDataContainer {

    private static final long serialVersionUID = -5293824177708202797L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitRoleType roleName;

    public UnitRoleType getRoleName() {
        return roleName;
    }

    public void setRoleName(UnitRoleType roleName) {
        this.roleName = roleName;
    }

}
