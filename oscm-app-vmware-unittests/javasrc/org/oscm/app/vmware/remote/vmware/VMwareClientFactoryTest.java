/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2011 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Author: Dirk Bernsau                                                      
 *                                                                              
 *  Creation Date: Jul 12, 2012                                                      
 *                                                                              
 *  Completion Time: Jul 12, 2012                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.app.vmware.remote.vmware;

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.oscm.app.vmware.exception.ValidationException;
import org.oscm.app.vmware.persistence.DataAccessService;
import org.oscm.app.vmware.persistence.VMwareCredentials;
import org.oscm.app.vmware.remote.vmware.VMwareClientFactory;

/**
 * @author Dirk Bernsau
 * 
 */
public class VMwareClientFactoryTest {

    private VMwareClientFactory factory;

    @Before
    public void before() {
        factory = new VMwareClientFactory("locale");
        factory.das = mock(DataAccessService.class);
    }

    @Test(expected = ValidationException.class)
    public void validateState_missingUrl() throws Exception {
        // given
        String url = null;
        String vcenter = "vcenter";
        String datacenter = "datacenter";
        String cluster = "cluster";
        VMwareCredentials credentials = new VMwareCredentials("http://vcenter",
                "userId", "password");

        // when
        factory.validateState(vcenter, credentials);

        // then expect validation error
    }

    @Test(expected = ValidationException.class)
    public void validateState_missingUserId() throws Exception {
        // given
        String url = "url";
        String vcenter = "vcenter";
        String datacenter = "datacenter";
        String cluster = "cluster";
        VMwareCredentials credentials = new VMwareCredentials(null, null,
                "password");

        // when
        factory.validateState(vcenter, credentials);

        // then expect validation error
    }

    @Test(expected = ValidationException.class)
    public void validateState_missingPassword() throws Exception {
        // given
        String url = "url";
        String vcenter = "vcenter";
        String datacenter = "datacenter";
        String cluster = "cluster";
        VMwareCredentials credentials = new VMwareCredentials(null, "userid",
                null);

        // when
        factory.validateState(vcenter, credentials);

        // then expect validation error
    }

}
