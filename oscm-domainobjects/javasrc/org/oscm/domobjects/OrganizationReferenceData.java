/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 25.02.2011                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import org.oscm.domobjects.converters.ORefTConverter;
import org.oscm.domobjects.enums.OrganizationReferenceType;

/**
 * @author weiser
 * 
 */
@Embeddable
public class OrganizationReferenceData extends DomainDataContainer {

    private static final long serialVersionUID = -66626743587180388L;

    @Convert(converter = ORefTConverter.class)
    @Column(nullable = false)
    private OrganizationReferenceType referenceType;

    public void setReferenceType(OrganizationReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public OrganizationReferenceType getReferenceType() {
        return referenceType;
    }

}
