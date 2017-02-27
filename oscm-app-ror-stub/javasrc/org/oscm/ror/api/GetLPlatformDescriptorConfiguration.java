/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 30.01.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror.api;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author kulle
 * 
 */
@XmlRootElement(name = "GetLPlatformDescriptorConfigurationResponse")
public class GetLPlatformDescriptorConfiguration extends Response implements
        Serializable {

    private static final long serialVersionUID = 1L;

    LPlatformDescriptor lplatformdescriptor;

    public LPlatformDescriptor getLplatformdescriptor() {
        return lplatformdescriptor;
    }

    @XmlElement
    public void setLplatformdescriptor(LPlatformDescriptor lplatformdescriptor) {
        this.lplatformdescriptor = lplatformdescriptor;
    }

}
