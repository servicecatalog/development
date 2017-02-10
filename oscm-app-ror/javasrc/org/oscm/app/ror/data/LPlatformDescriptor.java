/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 2013-04-18                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror.data;

import org.apache.commons.configuration.HierarchicalConfiguration;

import org.oscm.app.iaas.data.VSystemTemplate;

/**
 * Value object describing L-Platform templates.
 * 
 */
public class LPlatformDescriptor implements VSystemTemplate {

    private HierarchicalConfiguration configuration;

    public LPlatformDescriptor(HierarchicalConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * @return the unique descriptor ID
     */
    public String getVSystemTemplateId() {
        return configuration.getString("lplatformdescriptorId");
    }

    /**
     * Returns the name of the template.
     * 
     * @return the name
     */
    public String getVSystemTemplateName() {
        return configuration.getString("lplatformdescriptorName");
    }

    /**
     * Returns a description of the L-Platform template.
     * 
     * @return the description
     */
    public String getDescription() {
        return configuration.getString("description");
    }

    /**
     * Returns the tenant name of the person who created the L-Platform
     * template.
     * 
     * @return the tenant name
     */
    public String getCreatorName() {
        return configuration.getString("creatorName");
    }

    /**
     * Returns the ID of the person who registered the L-Platform template.
     * 
     * @return the person ID
     */
    public String getRegistrant() {
        return configuration.getString("registrant");
    }
}
