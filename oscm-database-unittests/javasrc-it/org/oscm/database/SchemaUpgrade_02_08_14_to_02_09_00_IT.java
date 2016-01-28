package org.oscm.database;

import java.net.URL;

import org.oscm.setup.DatabaseVersionInfo;

/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                  
 *                                                                                                                                 
 *  Creation Date: 2014-06-05                                                      
 *                                                                              
 *******************************************************************************/

/**
 * @author goebel
 * 
 */
public class SchemaUpgrade_02_08_14_to_02_09_00_IT extends
        SchemaUpgradeTestBase {

    public SchemaUpgrade_02_08_14_to_02_09_00_IT() {
        super(new DatabaseVersionInfo(2, 8, 2),
                new DatabaseVersionInfo(2, 9, 0));
    }

    @Override
    protected URL getSetupDataset() {
        return getClass().getResource("/setup_02_08_14_to_02_09_00.xml");
    }

    @Override
    protected URL getExpectedDataset() {
        return getClass().getResource("/expected_02_08_14_to_02_09_00.xml");
    }

}