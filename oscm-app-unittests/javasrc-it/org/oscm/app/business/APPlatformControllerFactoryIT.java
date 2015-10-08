/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2015 
 *******************************************************************************/

package org.oscm.app.business;

import javax.naming.InitialContext;

import org.junit.Test;
import org.mockito.Mockito;

import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;
import org.oscm.app.business.APPlatformControllerFactory;
import org.oscm.app.v1_0.exceptions.ControllerLookupException;
import org.oscm.app.v1_0.intf.APPlatformController;

/**
 * Unit tests for {@link APPlatformControllerFactory};
 * 
 * @author hoffmann
 */
public class APPlatformControllerFactoryIT extends EJBTestBase {

    @Override
    protected void setup(TestContainer container) throws Exception {
    }

    @Test(expected = ControllerLookupException.class)
    public void testWrongInterface() throws Exception {

        InitialContext context = new InitialContext();
        context.bind(APPlatformController.JNDI_PREFIX + "test.controller",
                new String());
        APPlatformControllerFactory.getInstance("test.controller");
    }

    @Test
    public void testOK() throws Exception {

        new APPlatformControllerFactory(); // coverage
        InitialContext context = new InitialContext();
        context.bind(APPlatformController.JNDI_PREFIX + "test.controller",
                Mockito.mock(APPlatformController.class));
        APPlatformControllerFactory.getInstance("test.controller");
    }

    @Test(expected = ControllerLookupException.class)
    public void testNotBound() throws Exception {
        APPlatformControllerFactory.getInstance("not-existing.controller");
    }

}
