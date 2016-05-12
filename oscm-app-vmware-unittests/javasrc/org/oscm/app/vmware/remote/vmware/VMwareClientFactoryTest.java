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
        VMwareCredentials credentials = new VMwareCredentials(url,
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
        VMwareCredentials credentials = new VMwareCredentials(url, null,
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
        VMwareCredentials credentials = new VMwareCredentials(url, "userid",
                null);

        // when
        factory.validateState(vcenter, credentials);

        // then expect validation error
    }

}
