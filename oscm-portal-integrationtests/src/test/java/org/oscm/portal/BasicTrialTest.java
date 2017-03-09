
/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: Feb 7, 2017
 *
 *******************************************************************************/

package org.oscm.portal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
/**
 * Test suite for integration web tests for the OSCM portal
 *
 * @author miethaner
 */
@SuiteClasses({ PortalTestWT.class})
public class BasicTrialTest    {

    public static String supplierOrgId;
    public static String customerOrgId;
}
