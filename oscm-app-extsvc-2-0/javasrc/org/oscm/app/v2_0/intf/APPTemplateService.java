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
import org.oscm.app.v2_0.data.Template;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.v2_0.exceptions.AuthenticationException;

/**
 * Interface providing methods by which service controllers implemented in APP
 * can access APPs template service.
 */
@Remote
public interface APPTemplateService {

    /**
     * The JNDI name with which the APP template service is registered in the
     * container.
     */
    public static final String JNDI_NAME = "java:global/oscm-app/oscm-app/APPTemplateServiceBean!org.oscm.app.v2_0.intf.APPTemplateService";

    /**
     * Saves the template with the given name and content for the controller.
     * 
     * @param template
     *            the template with name and content
     * @param controllerId
     *            the owning controller id
     * @param authentication
     *            a <code>PasswordAuthentication</code> object identifying a
     *            technology manager registered in the organization which is
     *            responsible for the controller
     * @throws AuthenticationException
     *             if the authentication of the user fails
     * @throws APPlatformException
     *             if a general problem occurs in accessing APP
     */
    public void saveTemplate(Template template, String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException;

    /**
     * Delete the template with the given name for the controller.
     * 
     * @param fileName
     *            the template name
     * @param controllerId
     *            the owning controller id
     * @param authentication
     *            a <code>PasswordAuthentication</code> object identifying a
     *            technology manager registered in the organization which is
     *            responsible for the controller
     * @throws AuthenticationException
     *             if the authentication of the user fails
     * @throws APPlatformException
     *             if a general problem occurs in accessing APP
     */
    public void deleteTemplate(String fileName, String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException;

    /**
     * Gets all templates for the given controller id.
     * 
     * @param controllerId
     *            the owning controller id
     * @param authentication
     *            a <code>PasswordAuthentication</code> object identifying a
     *            technology manager registered in the organization which is
     *            responsible for the controller
     * @throws AuthenticationException
     *             if the authentication of the user fails
     * @throws APPlatformException
     *             if a general problem occurs in accessing APP
     */
    public List<Template> getTemplateList(String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException;

    /**
     * Gets the template for the given file name and the controller id.
     * 
     * @param fileName
     *            the template name
     * @param controllerId
     *            the owning controller id
     * @param authentication
     *            a <code>PasswordAuthentication</code> object identifying a
     *            technology manager registered in the organization which is
     *            responsible for the controller
     * @throws AuthenticationException
     *             if the authentication of the user fails
     * @throws APPlatformException
     *             if a general problem occurs in accessing APP
     */
    public Template getTemplate(String fileName, String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException;
}
