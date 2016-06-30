/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 18, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.oscm.rest.common.CommonFilterFactory;

import com.sun.jersey.spi.container.ResourceFilter;

/**
 * Unit test for CommonFilterFactory
 * 
 * @author miethaner
 */
public class CommonFilterFactoryTest {

    @Test
    public void testCommonFilterFactory() {
        CommonFilterFactory factory = new CommonFilterFactory();

        List<ResourceFilter> list = factory.create(null);

        assertNotNull(list);
        assertEquals(2, list.size());
    }

}
