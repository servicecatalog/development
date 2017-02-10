/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 2013-03-28                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.iaas.data;

import java.util.List;

/**
 * Represents configuration details of a virtual system template.
 */
public interface VSystemTemplateConfiguration extends VSystemTemplate {

    public List<Network> getNetworks();

    public List<? extends VServerConfiguration> getVServers();
}
