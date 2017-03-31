/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: Mar 31, 2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.v2_0.intf;

/**
 * Interface for template files for controllers.
 * 
 * @author miethaner
 */
public interface Template {

    /**
     * Gets the file name of the template including its suffix.
     * 
     * @return the file name
     */
    public String getFileName();

    /**
     * The content of the template file in plain text.
     * 
     * @return the content
     */
    public String getContent();
}
