/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 10.02.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author kulle
 * 
 */
@XmlRootElement(name = "GetLPlatformStatus")
public class LplatformStatus extends Response {

    private transient CreateLPlatform lplatform;

    public LplatformStatus() {

    }

    public LplatformStatus(CreateLPlatform lplatform) {
        this.lplatform = lplatform;
    }

    /**
     * One of:<br />
     * NORMAL: The system is operating normally<br/>
     * RECONFIG_ING: The system is being reconfigured<br />
     * DEPLOYING: The system is being deployed<br />
     * ERROR: A system error has occurred
     */
    public String getLplatformStatus() {
        return lplatform.getLplatformStatus();
    }

    @XmlElement
    public void setLplatformStatus(String lplatformStatus) {
        lplatform.setLplatformStatus(lplatformStatus);
    }

}
