/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 20.07.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.vo;

/**
 * Represents organizational unit and provides data related to it.
 */
public class VOOrganizationalUnit extends BaseVO {

    private static final long serialVersionUID = 3901367900656380907L;

    /**
     * referenceId.
     */
    private String referenceId;

    /**
     * The organizational unit description.
     */
    private String description;

    /**
     * The organizational unit name.
     */
    private String name;

    /**
     * Used to mark if organizational unit is default unit.
     */
    private boolean defaultGroup;

    /**
     * The organizational unit id. Uniquely identifies unit in the platform.
     * (business key).
     */
    private String organizationId;

    /**
     * Sets the referenceId.
     * 
     * @param referenceId
     *            - the referenceId
     */
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * Sets the description of the unit.
     * 
     * @param description
     *            - the description of the unit
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the name of the organizational unit.
     * 
     * @param name
     *            - the name of unit
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Marks unit as default.
     * 
     * @param defaultGroup
     *            - <code>true</code> if unit should be the default unit,
     *            <code>false</code> otherwise
     */
    public void setDefaultGroup(boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    /**
     * Sets the unique organization id.
     * 
     * @param organizationId
     *            - The unique organizationId
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * Returns the referenceId.
     * 
     * @return - the referenceId
     */
    public String getReferenceId() {
        return referenceId;
    }

    /**
     * Returns description of the organizational unit.
     * 
     * @return - the description of the unit
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the name of organizational unit.
     * 
     * @return - the name of the unit
     */
    public String getName() {
        return name;
    }

    /**
     * Returns information if given unit is a default unit.
     * 
     * @return - <code>true</code> if unit is a default unit, <code>false</code>
     *         otherwise
     */
    public boolean isDefaultGroup() {
        return defaultGroup;
    }

    /**
     * Returns unique organizationId. It is unique identified or unit in the
     * platform.
     * 
     * @return - the unique organizationId
     */
    public String getOrganizationId() {
        return organizationId;
    }
}
