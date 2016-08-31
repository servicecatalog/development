/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 20.09.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.internal.usermanagement;

/**
 * Maps a user to the subscriptions that are available (reading) or shall be
 * assigned (writing).
 * 
 * @author weiser
 * 
 */
public class POUserAndOrganization extends POUser {

    private static final long serialVersionUID = 279072057940068106L;

    private String userId;
    private String email;
    private String organizationName;
    private String organizationId;


    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }

    @Override
    public String getEmail() {
        return super.getEmail();
    }

}
