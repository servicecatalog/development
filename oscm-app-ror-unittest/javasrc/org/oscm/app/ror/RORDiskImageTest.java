/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                             
 *                                                                                                                                 
 *  Creation Date: Mar 5, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.Test;

import org.oscm.app.ror.data.RORDiskImage;

/**
 * @author zhaohang
 * 
 */
public class RORDiskImageTest {

    private RORDiskImage rorDiskImage;

    private static final String DISKIMAGEID = "DiskImageId";
    private static final String DISKIMAGENAME = "DiskImageName";

    @Test
    public void getDiskImageId() {
        // given
        HierarchicalConfiguration configuration = mock(HierarchicalConfiguration.class);
        when(configuration.getString(eq("diskimageId")))
                .thenReturn(DISKIMAGEID);

        // when
        rorDiskImage = new RORDiskImage(configuration);
        String result = rorDiskImage.getDiskImageId();

        // then
        assertEquals(result, DISKIMAGEID);
    }

    @Test
    public void getDiskImageName() {
        // given
        HierarchicalConfiguration configuration = mock(HierarchicalConfiguration.class);
        when(configuration.getString(eq("diskimageName"))).thenReturn(
                DISKIMAGENAME);

        // when
        rorDiskImage = new RORDiskImage(configuration);
        String result = rorDiskImage.getDiskImageName();

        // then
        assertEquals(result, DISKIMAGENAME);

    }

}
