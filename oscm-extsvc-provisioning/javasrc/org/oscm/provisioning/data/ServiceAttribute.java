/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *       
 *  Creation Date: 2010-07-07                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.provisioning.data;

/**
 * Provides information on a service attribute. An application can evaluate the
 * information and perform the corresponding actions, for example, overwrite the
 * credentials of the controller.
 */
public class ServiceAttribute {

    /**
     * The identifier of the attribute.
     * 
     */
    private String attributeId;

    /**
     * The value of the attribute.
     * 
     */
    private String value;

    /**
     * Retrieves the identifier of the attribute.
     * 
     * @return the parameter ID
     */
    public String getAttributeId() {
        return attributeId;
    }

    /**
     * Sets the identifier of the attribute.
     * 
     * @param attributeId
     *            the attribute ID
     */
    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    /**
     * Retrieves the value of the attribute.
     * 
     * @return the attribute value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the attribute.
     * 
     * @param value
     *            the attribute value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
