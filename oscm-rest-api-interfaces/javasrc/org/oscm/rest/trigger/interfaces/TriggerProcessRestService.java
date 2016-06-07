/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 25, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.interfaces;

import java.util.List;
import java.util.UUID;

import javax.ejb.Remote;

/**
 * @author miethaner
 *
 */
@Remote
public interface TriggerProcessRestService {

    public UUID createProcess(TriggerProcessRest process);

    public void deleteProcess(UUID id);

    public void updateProcess(TriggerProcessRest process);

    public TriggerProcessRest getProcess(UUID id);

    public List<TriggerProcessRest> getProcesses();

    public void approve(TriggerProcessRest process);

    public void reject(TriggerProcessRest process);

    public void cancel(TriggerProcessRest process);

}
