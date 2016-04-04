/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2015
 *
 *******************************************************************************/
package org.oscm.converter.api;

import org.oscm.dataservice.local.DataService;

public interface DataServiceHolder {
    void setDataService(DataService dataService);

    DataService getDataService();
}
