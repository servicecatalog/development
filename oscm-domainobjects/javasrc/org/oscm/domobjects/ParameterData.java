/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: Peter Pock                                                      
 *                                                                              
 *  Creation Date: 29.06.2009                                                      
 *                                                                              
 *  Completion Time: 30.06.2009                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
/**
 * Data container to hold the information of a product parameter.
 * 
 * @author Peter Pock
 * 
 */
@Embeddable
public class ParameterData extends DomainDataContainer {

    private static final long serialVersionUID = -2165507867844651769L;
    @Field(analyzer = @Analyzer(definition = "customanalyzer"))
    private String value;

    @Column(nullable = false)
    private boolean configurable;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setConfigurable(boolean configurable) {
        this.configurable = configurable;
    }

    public boolean isConfigurable() {
        return configurable;
    }

}
