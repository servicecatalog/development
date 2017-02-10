/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 07.02.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror.api;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author kulle
 * 
 */
public abstract class Response {

    private String responseMessage = "PAPI00000 Processing was completed";
    private String responseStatus = "SUCCESS";

    public String getResponseMessage() {
        return responseMessage;
    }

    @XmlElement
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    @XmlElement
    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

}
