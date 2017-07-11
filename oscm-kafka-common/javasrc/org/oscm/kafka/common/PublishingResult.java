/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 03.07.17 14:39
 *
 ******************************************************************************/

package org.oscm.kafka.common;

public enum PublishingResult {

    SUCCESS, ERROR;

    public boolean isError() {
        return this.equals(ERROR);
    }

    public boolean isSuccess() {
        return !isError();
    }
    
}
