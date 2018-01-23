/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2018
 *
 *  Creation Date: 23.01.18 14:58
 *
 ******************************************************************************/
package org.oscm.database;

import java.net.URL;

import org.oscm.setup.DatabaseVersionInfo;

public class SchemaUpgrade_02_09_11_to_02_09_13_IT
        extends SchemaUpgradeTestBase {

    public SchemaUpgrade_02_09_11_to_02_09_13_IT() {
        super(new DatabaseVersionInfo(2, 9, 11),
                new DatabaseVersionInfo(2, 9, 13));
    }

    @Override
    protected URL getSetupDataset() {
        return getClass().getResource("/setup_02_09_11_to_02_09_13.xml");
    }

    @Override
    protected URL getExpectedDataset() {
        return getClass().getResource("/expected_02_09_11_to_02_09_13.xml");
    }

}