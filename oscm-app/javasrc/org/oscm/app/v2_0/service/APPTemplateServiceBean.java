/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: Mar 31, 2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.v2_0.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.oscm.app.dao.TemplateFileDAO;
import org.oscm.app.domain.TemplateFile;
import org.oscm.app.v2_0.data.PasswordAuthentication;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.v2_0.exceptions.AuthenticationException;
import org.oscm.app.v2_0.intf.APPTemplateService;
import org.oscm.app.v2_0.intf.Template;

/**
 * Implementation of the template service.
 * 
 * @author miethaner
 */
public class APPTemplateServiceBean implements APPTemplateService {

    @EJB
    protected APPAuthenticationServiceBean authService;

    @EJB
    protected TemplateFileDAO templateDAO;

    @Override
    public void saveTemplate(String fileName, String content,
            String controllerId, PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException {

        authService.authenticateTMForController(controllerId, authentication);

        templateDAO.saveTemplateFile(
                new TemplateFile(0L, fileName, content, controllerId));
    }

    @Override
    public void deleteTemplate(String fileName, String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException {

        authService.authenticateTMForController(controllerId, authentication);

        templateDAO.deleteTemplateFile(
                new TemplateFile(0L, fileName, "", controllerId));
    }

    @Override
    public List<Template> getTemplateList(String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException {

        authService.authenticateTMForController(controllerId, authentication);

        return new ArrayList<Template>(
                templateDAO.getTemplateFilesByControllerId(controllerId));
    }

    @Override
    public Template getTemplate(String fileName, String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException {

        authService.authenticateTMForController(controllerId, authentication);

        return templateDAO.getTemplateFileByUnique(fileName, controllerId);
    }

}
