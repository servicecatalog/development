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
@XmlType(name = "lplatform")
public class LPlatform implements Serializable {

    private static final long serialVersionUID = 17L;

    private String lplatformId = "lplatformId_";

    private String lplatformStatus = "RECONFIG_ING";

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

    @XmlElement
    public void setLplatformStatus(String lplatformStatus) {
        this.lplatformStatus = lplatformStatus;
    }

}
