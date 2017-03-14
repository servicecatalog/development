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
@XmlRootElement(name = "ListLPlatformResponse")
public class ListLPlatform extends Response implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<LPlatform> lplatforms;

    public List<LPlatform> getLplatforms() {
        return lplatforms;
    }

    @XmlElement
    public void setLplatforms(List<LPlatform> lplatforms) {
        this.lplatforms = lplatforms;
    }

}
