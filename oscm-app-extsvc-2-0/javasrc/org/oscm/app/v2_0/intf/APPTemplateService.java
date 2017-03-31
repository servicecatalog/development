/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: Mar 31, 2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.v2_0.intf;

import java.util.List;

import javax.ejb.Remote;

import org.oscm.app.v2_0.data.PasswordAuthentication;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.v2_0.exceptions.AuthenticationException;

/**
 * Interface providing methods by which service controllers implemented in APP
 * can access APPs template service.
 */
@Remote
public interface APPTemplateService {

    public void saveTemplate(String fileName, String content,
            String controllerId, PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException;

    public void deleteTemplate(String fileName, String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException;

    public List<Template> getTemplateList(String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException;

    public Template getTemplate(String fileName, String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException;
}
