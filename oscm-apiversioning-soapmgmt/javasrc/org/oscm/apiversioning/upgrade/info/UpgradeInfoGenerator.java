/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Jan 30, 2015                                                   
 *                                                                              
 *******************************************************************************/

package org.oscm.apiversioning.upgrade.info;

/**
 * @author qiu
 * 
 */
public interface UpgradeInfoGenerator {

    public VORecords generateVORecords();

    public ServiceRecords generateRequestRecords();

    public ServiceRecords generateResponseRecords();

    public ServiceRecords generateExceptionRecords();
}
