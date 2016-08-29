/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Author: badziakp
 *
 *  Creation Date: 29.08.2016
 *
 *  Completion Time:
 *
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class TenantData extends DomainDataContainer implements
    Serializable {

    @Column(nullable = false)
    private String tenantId;

    private String description;

    private String idp;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdp() {
        return idp;
    }

    public void setIdp(String idp) {
        this.idp = idp;
    }
}
