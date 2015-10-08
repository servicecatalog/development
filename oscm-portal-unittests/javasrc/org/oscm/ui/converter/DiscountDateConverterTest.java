/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Author: Aleh Khomich                                         
 *                                                                              
 *  Creation Date: 26.05.2010                                                      
 *                                                                              
 *  Completion Time: 26.05.2010                                    
 *                                                                              
 *******************************************************************************/
package org.oscm.ui.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.convert.ConverterException;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import org.oscm.ui.stubs.FacesContextStub;

/**
 * JUnit tests for DiscountDateConverte class.
 * 
 * @author Aleh Khomich.
 * 
 */
public class DiscountDateConverterTest {

    private FacesContextStub context;
    private DiscountDateConverter converter;

    /**
     * Setup method.
     */
    @Before
    public void setup() {
        converter = new DiscountDateConverter();
        context = new FacesContextStub(Locale.ENGLISH);
    }

    /**
     * Test for getting value.
     * 
     * @throws ParseException
     */
    @Test
    public void testGetAsObject() throws ConverterException, ParseException {
        // LG: The DiscountDateConverter uses the default timezone to convert
        // strings in dates and
        // vice versa. Therefore the test case must also use this timezone.
        // "05/2010" is different from "1272664800000" for other timezones,
        // .f.e. GMT+8 at FNST.
        String expectedString = "05/2010";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        Date expectedDate = sdf.parse("05/2010");
        UIComponent component = ConverterTestHelper.getComponent(false, null,
                null, "discountDate");
        Long expected = new Long(expectedDate.getTime());

        Long actual = (Long) converter.getAsObject(context, component,
                expectedString);

        Assert.assertEquals(expected, actual);
    }

    /**
     * Test for getting string of the value.
     */
    @Test
    public void testGetAsString() {
        Long expectedLong = Long.valueOf(1272664800000L); // "05/2010"
        String expected = "05/2010";

        UIComponent component = ConverterTestHelper.getComponent(false, null,
                null, "discountDate");

        String actual = converter.getAsString(context, component, expectedLong);

        Assert.assertEquals(expected, actual);
    }

}
