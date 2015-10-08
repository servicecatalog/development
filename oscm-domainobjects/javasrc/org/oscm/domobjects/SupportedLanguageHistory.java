/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 2013-11-6                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * History object for SupportedLanguage
 * 
 * @author Zou
 */
@Entity
@NamedQueries({ @NamedQuery(name = "SupportedLanguageHistory.findByObject", query = "select sl from SupportedLanguageHistory sl where sl.objKey=:objKey order by objversion") })
public class SupportedLanguageHistory extends
        DomainHistoryObject<SupportedLanguageData> {

    private static final long serialVersionUID = 7047108191422227284L;

    public SupportedLanguageHistory() {
        dataContainer = new SupportedLanguageData();
    }

    public SupportedLanguageHistory(SupportedLanguage domObj) {
        super(domObj);
    }

}
