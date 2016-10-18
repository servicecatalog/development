/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 2016-10-11
 *
 *******************************************************************************/
package org.oscm.database;

import java.net.URL;

import org.oscm.setup.DatabaseVersionInfo;

public class SchemaUpgrade_02_09_04_to_02_09_05_IT extends
        SchemaUpgradeTestBase {

    public SchemaUpgrade_02_09_04_to_02_09_05_IT() {
        super(new DatabaseVersionInfo(2, 9, 3),
                new DatabaseVersionInfo(2, 9, 4));
    }

    @Override
    protected URL getSetupDataset() {
        return getClass().getResource("/setup_02_09_03_to_02_09_04.xml");
    }

    @Override
    protected URL getExpectedDataset() {
        return getClass().getResource("/expected_02_09_03_to_02_09_04.xml");
    }

}