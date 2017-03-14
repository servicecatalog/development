/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 03.02.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror.api;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author afschar
 */
@XmlRootElement(name = "ListLPlatformDescriptorResponse")
public class ListLPlatformDescriptor extends Response implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<LPlatformDescriptor> lplatformdescriptors;

    public List<LPlatformDescriptor> getLplatformdescriptors() {
        return lplatformdescriptors;
    }

    @XmlElement
    public void setLplatformdescriptors(
            List<LPlatformDescriptor> lplatformdescriptors) {
        this.lplatformdescriptors = lplatformdescriptors;
    }

}
