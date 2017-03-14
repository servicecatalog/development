/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: Enes Sejfi                      
 *                                                                              
 *  Creation Date: 11.06.2012                                                
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.oscm.types.enumtypes.FillinCriterion;

/**
 * JPA managed entity representing the PublicLandingpage data.
 */
@Embeddable
public class PublicLandingpageData extends DomainDataContainer implements
        Serializable {

    private static final long serialVersionUID = 226658413689731229L;

    /**
	 * number of services displayed on the public landingpage
	 */
    @Column(nullable = false)
    private int numberServices;

    /**
     * FillinCriterion specifies the criteria for filling in services if not
     * enough featured services are available
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FillinCriterion fillinCriterion;

    public int getNumberServices() {
        return numberServices;
    }

    public void setNumberServices(int numberServices) {
        this.numberServices = numberServices;
    }

    public FillinCriterion getFillinCriterion() {
        return fillinCriterion;
    }

    public void setFillinCriterion(FillinCriterion fillinCriterion) {
        this.fillinCriterion = fillinCriterion;
    }
}
