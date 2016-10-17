/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Author: Paulina Badziak
 *
 *  Creation Date: 30.08.2016
 *
 *  Completion Time: 30.08.2016
 *
 *******************************************************************************/
package org.oscm.domobjects;

import org.oscm.internal.types.enumtypes.IdpSettingType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


@Embeddable
public class TenantSettingData extends DomainDataContainer {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdpSettingType name;

    private String value;

    public IdpSettingType getName() {
        return name;
    }

    public void setName(IdpSettingType name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
