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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author kulle
 */
@XmlRootElement(name = "CreateLPlatformResponse")
public class CreateLPlatform extends Response implements Serializable {

    private static final long serialVersionUID = 1L;

    private volatile String lplatformId;

    private volatile String lplatformStatus = "RECONFIG_ING";

    public CreateLPlatform() {
        lplatformId = "lplatformId_" + System.currentTimeMillis();
    }

    public String getLplatformId() {
        return lplatformId;
    }

    @XmlElement
    public void setLplatformId(String lplatformId) {
        this.lplatformId = lplatformId;
    }

    /**
     * One of:<br />
     * NORMAL: The system is operating normally<br/>
     * RECONFIG_ING: The system is being reconfigured<br />
     * DEPLOYING: The system is being deployed<br />
     * ERROR: A system error has occurred
     */
    public String getLplatformStatus() {
        return lplatformStatus;
    }

    public void setLplatformStatus(String lplatformStatus) {
        this.lplatformStatus = lplatformStatus;
    }

}
