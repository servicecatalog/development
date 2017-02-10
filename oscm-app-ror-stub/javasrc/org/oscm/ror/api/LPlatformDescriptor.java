/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 03.02.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror.api;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author afschar
 */
@XmlType(name = "lplatformdescriptor")
public class LPlatformDescriptor implements Serializable {

    private static final long serialVersionUID = 1L;

    private String creatorName = "xyz";

    private String description = "xyz";

    private String registrant = "xyz";

    private String lplatformdescriptorId = "xyz";

    private String lplatformdescriptorName = "xyz";

    public String getCreatorName() {
        return creatorName;
    }

    @XmlElement
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getDescription() {
        return description;
    }

    @XmlElement
    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegistrant() {
        return registrant;
    }

    @XmlElement
    public void setRegistrant(String registrant) {
        this.registrant = registrant;
    }

    public String getLplatformdescriptorId() {
        return lplatformdescriptorId;
    }

    @XmlElement
    public void setLplatformdescriptorId(String lplatformdescriptorId) {
        this.lplatformdescriptorId = lplatformdescriptorId;
    }

    public String getLplatformdescriptorName() {
        return lplatformdescriptorName;
    }

    @XmlElement
    public void setLplatformdescriptorName(String lplatformdescriptorName) {
        this.lplatformdescriptorName = lplatformdescriptorName;
    }

}
