/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: 18.05.2009                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.databasesetup;

/**
 * @author pravi
 * 
 */
public class CSVGenerator extends Generator {

    String attributes[] = { KEY_CLIENT_NAME, KEY_USER_PREFIX, KEY_PASSWORD };

    /**
     * Prints some data to a file using a BufferedWriter
     */
    public String getData() {
        final StringBuilder sbuilder = new StringBuilder();
        final int maxValue = Integer
                .parseInt(getUserSetting(KEY_NUMBER_OF_USER));
        for (int ii = 0; ii < maxValue; ii++) {
            final String csv = getAttributeValues(attributes, ii);
            sbuilder.append(csv);
            sbuilder.append(NEW_LINE);
        }
        return sbuilder.toString();
    }

    private String getAttributeValues(final String attributes[], final int num) {
        final StringBuilder builder = new StringBuilder();
        for (int ii = 0; ii < attributes.length; ii++) {
            String key = attributes[ii];
            if (key.equals(KEY_USER_PREFIX)) {
                builder.append(SetupHelper.getUserID(num,
                        getUserSetting(KEY_USER_PREFIX)));
            } else {
                builder.append(getUserSetting(key));
            }

            if (ii < attributes.length - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

}
