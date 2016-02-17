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
public class LDIFGenerator extends Generator {

    final private String SYSTEM = "system";

    final private String ADMIN_GROUP = "OrganizationAdmin";

    final private String PLATFORM_USER = "PlatformUsers";

    /**
	 * 
	 */
    public String getData() {
        // extract the values
        final int maxValue = Integer
                .parseInt(getUserSetting(KEY_NUMBER_OF_USER));
        final String password = getUserSetting(KEY_PASSWORD);

        final StringBuilder sbuilder = new StringBuilder();
        for (int ii = 0; ii < maxValue; ii++) {
            final String ldifValues = getLDIFValues(ii, password);
            sbuilder.append(ldifValues);
        }

        return sbuilder.toString();
    }

    /**
     * 
     * @param serialNum
     * @param password
     * @return
     */
    private String getLDIFValues(final int serialNum, final String password) {

        final String userID = SetupHelper.getUserID(serialNum,
                getUserSetting(KEY_USER_PREFIX));

        final StringBuilder sbuild = new StringBuilder();

        sbuild.append("dn: cn=");
        if (serialNum == 0) {
            sbuild.append(ADMIN_GROUP);
        } else {
            sbuild.append("" + serialNum);
        }
        sbuild.append(",ou=" + PLATFORM_USER + ",ou=" + SYSTEM + "\n");
        sbuild.append("changetype: add\n");
        sbuild.append("objectclass: person\n");
        sbuild.append("objectclass: top\n");

        sbuild.append("cn: " + userID + "\n");
        sbuild.append("sn: TestUser\n");
        sbuild.append("userpassword:: " + password + "\n\n");

        if (serialNum == 0) {
            addUserToAdminGroup(sbuild, serialNum);
        }

        return sbuild.toString();
    }

    /**
     * 
     * @param sbuild
     * @param serialNumber
     */
    private void addUserToAdminGroup(final StringBuilder sbuild,
            final int serialNumber) {
        sbuild.append("dn: cn=" + ADMIN_GROUP + ",ou=" + PLATFORM_USER + ",ou="
                + SYSTEM + "\n");
        sbuild.append("changetype: modify\n");
        sbuild.append("add: uniquemember\n");
        sbuild.append("uniquemember: cn=" + serialNumber + ",ou="
                + PLATFORM_USER + ",ou=" + SYSTEM + "\n\n");
    }

}
