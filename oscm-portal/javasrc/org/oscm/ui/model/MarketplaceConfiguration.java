/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *           
 *  Author: Zou
 *                                                                                  
 *  Creation Date: 14.03.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.model;

/**
 * UI model for Marketplace configurations(tagging, review, social bookmark)
 */
public class MarketplaceConfiguration {

    private boolean taggingEnabled = true;

    private boolean reviewEnabled = true;

    private boolean socialBookmarkEnabled = true;

    private boolean categoriesEnabled;

    private boolean restricted;

    /**
     * Indicating if the tag cloud is shown.
     * 
     * @return the taggingEnabled
     */
    public boolean isTaggingEnabled() {
        return taggingEnabled;
    }

    /**
     * Set if the tag cloud should be shown.
     * 
     * @param taggingEnabled
     *            the taggingEnabled to set
     */
    public void setTaggingEnabled(boolean taggingEnabled) {
        this.taggingEnabled = taggingEnabled;
    }

    /**
     * Indicating if the tag review/rating is shown.
     * 
     * @return the reviewEnabled
     */
    public boolean isReviewEnabled() {
        return reviewEnabled;
    }

    /**
     * Set if the tag review/rating should be shown.
     * 
     * @param reviewEnabled
     *            the reviewEnabled to set
     */
    public void setReviewEnabled(boolean reviewEnabled) {
        this.reviewEnabled = reviewEnabled;
    }

    /**
     * Indicating if the social bookmarks are shown.
     * 
     * @return the socialBookmarkEnabled
     */
    public boolean isSocialBookmarkEnabled() {
        return socialBookmarkEnabled;
    }

    /**
     * Set if the social bookmarks should be shown.
     * 
     * @param socialBookmarkEnabled
     *            the socialBookmarkEnabled to set
     */
    public void setSocialBookmarkEnabled(boolean socialBookmarkEnabled) {
        this.socialBookmarkEnabled = socialBookmarkEnabled;
    }

    /**
     * @param categoriesEnabled
     *            the categoriesEnabled to set
     */
    public void setCategoriesEnabled(boolean categoriesEnabled) {
        this.categoriesEnabled = categoriesEnabled;
    }

    /**
     * @return the categoriesEnabled
     */
    public boolean isCategoriesEnabled() {
        return categoriesEnabled;
    }

    /**
     * Returns true if the marketplace is restricted
     * 
     * @return the restricted
     */
    public boolean isRestricted() {
        return restricted;
    }

    /**
     * Sets if the marketplace is restricted
     * 
     * @param restricted
     *            the restricted to set
     */
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }
}
