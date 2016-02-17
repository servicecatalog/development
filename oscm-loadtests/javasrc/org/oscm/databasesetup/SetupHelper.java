/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: 20.05.2009                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.databasesetup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * @author pravi
 * 
 */
public class SetupHelper {

    public static String getTkey(final int serialnum) {
        return "" + serialnum;
    }

    public static String getUserID(final int serialnum, final String prefix) {
        return prefix + serialnum;
    }

    public static String getDateAsString(final String format) {
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(currentDate.getTime());
    }

    /**
     * @param attribute
     * @return
     */
    public static int getInt(final String attribute) {
        return TestSetupConstants.MapAttributes.getInt(attribute);
    }

    public static boolean has(String str) {
        return str != null && str.trim().length() > 0;
    }

    public static int getRandomNumber(final int num) {
        final Random random = new Random();
        return random.nextInt(num);
    }

}
