/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016
 *                                                                              
 *  Author: cheld                                                     
 *                                                                              
 *  Creation Date: 16.12.2011                                                      
 *                                                                              
 *  Completion Time: 16.12.2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.ess.ws.v1_7;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oscm.ess.ws.v1_7.base.ServiceFactory;
import org.oscm.ess.ws.v1_7.base.WebserviceTestBase;
import org.oscm.ess.ws.v1_7.base.WebserviceTestSetup;

import com.fujitsu.bss.vo.VOMarketplace;
import com.fujitsu.bss.vo.VOTag;
import com.fujitsu.bss.vo.VOTechnicalService;
import com.fujitsu.bss.intf.TagService;

/**
 * @author cheld
 * 
 */
public class TagServiceWSTest {

    private static WebserviceTestSetup setup;
    private static VOTechnicalService technicalService;

    private static TagService tagService;

    VOMarketplace mpLocal;

    @BeforeClass
    public static void setUp() throws Exception {
        // clean the mails
        WebserviceTestBase.getMailReader().deleteMails();
        setup = new WebserviceTestSetup();
        setup.createSupplier("Supplier1");
        technicalService = setup.createTechnicalService();
        setup.addTags(technicalService,
                Arrays.asList(new String[] { "aaa", "abb", "bbb", "ccc" }));
        tagService = ServiceFactory.getDefault()
                .getTagService(setup.getSupplierUserKey(),
                        WebserviceTestBase.DEFAULT_PASSWORD);

    }

    @Test
    public void getTagsByLocale() {
        List<VOTag> tags = tagService.getTagsByLocale("en");
        assertEquals(4, tags.size());
    }

    @Test
    public void getTagsByLocale_differentLocale() {
        List<VOTag> tags = tagService.getTagsByLocale("ja");
        assertEquals(0, tags.size());
    }

    @Test(expected = SOAPFaultException.class)
    public void getTagsByLocale_null() {
        List<VOTag> tags = tagService.getTagsByLocale(null);
        assertEquals(0, tags.size());
    }

    @Test
    public void getTagsByPattern_wildcard() {
        List<String> tags = tagService.getTagsByPattern("en", "a%", 5);
        assertEquals(2, tags.size());
    }

    @Test
    public void getTagsByPattern_wildcard2() {
        List<String> tags = tagService.getTagsByPattern("en", "_bb", 5);
        assertEquals(2, tags.size());
    }

    @Test
    public void getTagsByPattern_differentLocale() {
        List<String> tags = tagService.getTagsByPattern("ja", "_bb", 5);
        assertEquals(0, tags.size());
    }

    @Test
    public void getTagsByPattern_emptyPattern() {
        List<String> tags = tagService.getTagsByPattern("en", "", 5);
        assertEquals(0, tags.size());
    }

    @Test
    public void getTagsByPattern_limit() {
        List<String> tags = tagService.getTagsByPattern("en", "%", 3);
        assertEquals(3, tags.size());
    }

}
