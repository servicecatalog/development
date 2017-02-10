/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 15.02.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.oscm.domobjects.enums.PublishingAccess;

/**
 * @author stavreva
 * 
 */
@Embeddable
public class MarketplaceToOrganizationData extends DomainDataContainer {

    private static final long serialVersionUID = -8262011730647781904L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublishingAccess publishingAccess;

    public PublishingAccess getPublishingAccess() {
        return publishingAccess;
    }

    public void setPublishingAccess(PublishingAccess publishingAccess) {
        this.publishingAccess = publishingAccess;
    }

}
