/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 2013-04-29                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror.data;

import org.apache.commons.configuration.HierarchicalConfiguration;

import org.oscm.app.iaas.data.VSystemTemplateConfiguration;

/**
 * Value object describing L-Platform configurations.
 */
public class LPlatformDescriptorConfiguration extends LPlatformBase implements
        VSystemTemplateConfiguration {

    private HierarchicalConfiguration configuration;

    public LPlatformDescriptorConfiguration(
            HierarchicalConfiguration configuration) {
        super(configuration);
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
}
