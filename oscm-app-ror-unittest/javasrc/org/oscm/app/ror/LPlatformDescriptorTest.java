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

import org.oscm.app.ror.data.LPlatformDescriptor;

/**
 * @author zhaohang
 * 
 */
public class LPlatformDescriptorTest {

    private LPlatformDescriptor lPlatformDescriptor;
    private static final String DESCRIPTORID = "descriptorId";
    private static final String DESCRIPTORNAME = "descriptorName";
    private static final String DESCRIPTION = "description";
    private static final String CREATORNAME = "creatorName";
    private static final String REGISTRANT = "registrant";
    private static final String LPLATFORMDESCRIPTORID = "lplatformdescriptorId";
    private static final String LPLATFORMDESCRIPTORNAME = "lplatformdescriptorName";

    @Test
    public void getVSystemTemplateId() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                LPLATFORMDESCRIPTORID, DESCRIPTORID);

        // when
        prepareDescriptor(configuration);
        String result = lPlatformDescriptor.getVSystemTemplateId();

        // then
        assertEquals(result, DESCRIPTORID);
    }

    @Test
    public void getVSystemTemplateName() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                LPLATFORMDESCRIPTORNAME, DESCRIPTORNAME);

        // when
        prepareDescriptor(configuration);
        String result = lPlatformDescriptor.getVSystemTemplateName();

        // then
        assertEquals(result, DESCRIPTORNAME);
    }

    @Test
    public void getDescription() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                DESCRIPTION, DESCRIPTION);

        // when
        prepareDescriptor(configuration);
        String result = lPlatformDescriptor.getDescription();

        // then
        assertEquals(result, DESCRIPTION);
    }

    @Test
    public void getCreatorName() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                CREATORNAME, CREATORNAME);

        // when
        prepareDescriptor(configuration);
        String result = lPlatformDescriptor.getCreatorName();

        // then
        assertEquals(result, CREATORNAME);
    }

    @Test
    public void getRegistrant() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                REGISTRANT, REGISTRANT);

        // when
        prepareDescriptor(configuration);
        String result = lPlatformDescriptor.getRegistrant();

        // then
        assertEquals(result, REGISTRANT);
    }

    private HierarchicalConfiguration prepareConfiguration(String entryValue,
            String returnValue) {
        HierarchicalConfiguration configuration = mock(HierarchicalConfiguration.class);
        when(configuration.getString(eq(entryValue))).thenReturn(returnValue);

        return configuration;
    }

    private void prepareDescriptor(HierarchicalConfiguration configuration) {
        lPlatformDescriptor = new LPlatformDescriptor(configuration);
    }
}
