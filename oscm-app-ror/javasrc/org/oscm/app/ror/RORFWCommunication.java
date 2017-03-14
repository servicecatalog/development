/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 07.05.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.oscm.app.iaas.PropertyHandler;
import org.oscm.app.iaas.data.FWPolicy;
import org.oscm.app.iaas.data.NATRule;
import org.oscm.app.iaas.intf.FWCommunication;

/**
 * Add dummy EJB to satisfy deployment.
 * 
 * @author stavreva
 * 
 */
@Stateless
@Local(FWCommunication.class)
public class RORFWCommunication implements FWCommunication {

    @Override
    public Set<String> updateFirewallSetting(PropertyHandler properties,
            Set<FWPolicy> policies, Set<String> knownPolicyIds)
            throws Exception {
        return new HashSet<String>();
    }

    @Override
    public Set<String> updateNATSetting(PropertyHandler properties,
            Set<NATRule> rules) throws Exception {
        return new HashSet<String>();
    }

    @Override
    public Set<NATRule> getNATSetting(PropertyHandler properties)
            throws Exception {
        return new HashSet<NATRule>();
    }

    @Override
    public String getFirewallStatus(PropertyHandler properties)
            throws Exception {
        return null;
    }

    @Override
    public void startFirewall(PropertyHandler properties) throws Exception {
        return;
    }

}
