/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *       
 *  Creation Date: 2009-09-17                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.oscm.types.enumtypes.OfferingType;
import org.oscm.types.enumtypes.ServiceAccessType;
import org.oscm.types.enumtypes.ServiceStatus;

/**
 * Represents a marketable service based on a technical service.
 */
@XmlRootElement
public class VOService extends BaseVO implements Serializable {

    private static final long serialVersionUID = 2585748034132679028L;

    /**
     * The parameters set for the service.
     */
    private List<VOParameter> parameters = new ArrayList<VOParameter>();

    /**
     * The localized description of the service.
     */
    private String description;

    /**
     * The name of the service (long version).
     */
    private String name;

    /**
     * The short name that uniquely identifies the service in the entire
     * platform.
     */
    private String serviceId;

    /**
     * The identifier of the technical service the marketable service is based
     * on.
     */
    private String technicalId;

    /**
     * The URL of the technical service definition.
     */
    private String featureURL;

    /**
     * The URL of the underlying application's remote interface.
     */
    private String baseURL;

    /**
     * The price model of the service.
     */
    private VOPriceModel priceModel;

    /**
     * The status of the service.
     */
    private ServiceStatus status;

    /**
     * The access type defined for the service.
     */
    private ServiceAccessType accessType;

    /**
     * The identifier of the seller organization (supplier, broker, or reseller)
     * that provides the service.
     */
    private String sellerId;

    /**
     * The name of the seller organization that provides the service.
     */
    private String sellerName;

    /**
     * The numeric key of the seller organization that provides the service.
     */
    private long sellerKey;

    /**
     * The list of tags for the service.
     */
    private List<String> tags = new ArrayList<String>();

    /**
     * The localized short description of the service.
     */
    private String shortDescription;

    /**
     * The average of all customer ratings.
     */
    private BigDecimal averageRating;

    /**
     * The number of customer reviews for the service.
     */
    private int numberOfReviews;

    /**
     * The type of the seller organization that provides the service (broker,
     * reseller, or supplier)
     */
    private OfferingType offeringType;

    /**
     * The URL of an external tool for configuring the service parameters
     */
    private String configuratorUrl;

    /**
     * The URL of the custom tab on my subscriptions page
     */
    private String customTabUrl;

    /**
     * The Name of the custom tab on my subscriptions page
     */
    private String customTabName;

    /**
     * Retrieves the text describing the service.
     * 
     * @return the service description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the name of the service.
     * 
     * @return the service name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the name of the service that is shown to customers.
     * 
     * @return the display name
     */
    public String getNameToDisplay() {
        return getName();
    }

    /**
     * Retrieves the parameters defined for the service.
     * 
     * @return the list of parameters
     */
    public List<VOParameter> getParameters() {
        return parameters;
    }

    /**
     * Retrieves the price model defined for the service.
     * 
     * @return the price model
     */
    public VOPriceModel getPriceModel() {
        return priceModel;
    }

    /**
     * Retrieves the access type defined for the service. The access type
     * specifies how users access the underlying application.
     * 
     * @return the service access type
     */
    public ServiceAccessType getAccessType() {
        return accessType;
    }

    /**
     * Retrieves the identifier of the service.
     * 
     * @return the service ID
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Retrieves the URL of the definition of the underlying technical service.
     * 
     * @return the URL
     */
    public String getFeatureURL() {
        return featureURL;
    }

    /**
     * Retrieves the identifier of the technical service on which the marketable
     * service is based.
     * 
     * @return the technical service ID
     */
    public String getTechnicalId() {
        return technicalId;
    }

    /**
     * Sets the text describing the service.
     * 
     * @param marketingName
     *            the service description
     */
    public void setDescription(final String marketingName) {
        description = marketingName;
    }

    /**
     * Sets the name of the service.
     * 
     * @param name
     *            the service name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the given parameters for the service.
     * 
     * @param parameters
     *            the list of parameters
     */
    public void setParameters(final List<VOParameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * Assigns the given price model to the service.
     * 
     * @param priceModel
     *            the price model
     */
    public void setPriceModel(final VOPriceModel priceModel) {
        this.priceModel = priceModel;
    }

    /**
     * Sets the access type for the service. The access type specifies how users
     * access the underlying application.
     * 
     * @param accessType
     *            the service access type
     */
    public void setAccessType(final ServiceAccessType accessType) {
        this.accessType = accessType;
    }

    /**
     * Sets the identifier of the service.
     * 
     * @param serviceId
     *            the service ID
     */
    public void setServiceId(final String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Sets the URL of the definition of the underlying technical service.
     * 
     * @param technicalURL
     *            the URL
     */
    public void setFeatureURL(final String technicalURL) {
        this.featureURL = technicalURL;
    }

    /**
     * Retrieves the identifier of the technical service on which the marketable
     * service is based.
     * 
     * @param technicalId
     *            the technical service ID
     */
    public void setTechnicalId(final String technicalId) {
        this.technicalId = technicalId;
    }

    /**
     * Sets the status of the service.
     * 
     * @param status
     *            the status
     */
    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    /**
     * Retrieves the status of the service.
     * 
     * @return the status
     */
    public ServiceStatus getStatus() {
        return status;
    }

    /**
     * Retrieves the identifier of the seller organization (supplier, broker, or
     * reseller) that provides the service.
     * 
     * @return the organization ID
     */
    public String getSellerId() {
        return sellerId;
    }

    /**
     * Sets the identifier of the seller organization (supplier, broker, or
     * reseller) that provides the service.
     * 
     * @param sellerId
     *            the organization ID
     */
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * Sets the name of the seller organization (supplier, broker, or reseller)
     * that provides the service.
     * 
     * @param sellerName
     *            the organization name
     */
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    /**
     * Retrieves the name of the seller organization (supplier, broker, or
     * reseller) that provides the service.
     * 
     * @return the organization name
     */
    public String getSellerName() {
        return sellerName;
    }

    /**
     * Sets the numeric key of the seller organization (supplier, broker, or
     * reseller) that provides the service.
     * 
     * @param sellerKey
     *            the key
     */
    public void setSellerKey(long sellerKey) {
        this.sellerKey = sellerKey;
    }

    /**
     * Retrieves the numeric key of the seller organization (supplier, broker,
     * or reseller) that provides the service.
     * 
     * @return the key
     */
    public long getSellerKey() {
        return sellerKey;
    }

    /**
     * Retrieves the tags defined for the service.
     * 
     * @return the list of tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Sets the specified tags for the service.
     * 
     * @param serviceTags
     *            the list of tags
     */
    public void setTags(List<String> serviceTags) {
        tags = serviceTags;
    }

    /**
     * Retrieves the short description of the service.
     * 
     * @return the short description
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Sets the short description of the service.
     * 
     * @param shortDescription
     *            the short description
     */
    public void setShortDescription(final String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * Sets the given average customer rating for the service.
     * 
     * @param averageRating
     *            the average of all customer ratings
     */
    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Returns the average customer rating for the service.
     * 
     * @return the average of all customer ratings
     */
    public BigDecimal getAverageRating() {
        return averageRating;
    }

    /**
     * Returns the number of customer reviews for the service.
     * 
     * @return the number of reviews
     */
    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    /**
     * Sets the given number of customer reviews for the service.
     * 
     * @param numberOfReviews
     *            the number of reviews
     */
    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    /**
     * Returns the URL of the underlying application's remote interface.
     * 
     * @return the URL
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Sets the URL of the underlying application's remote interface.
     * 
     * @param baseURL
     *            the URL of the application<br>
     *            Be aware that internet domain names must follow the following
     *            rules: <br>
     *            They must start with a letter and end with a letter or number.<br>
     *            They may contain letters, numbers, or hyphens only. Special
     *            characters are not allowed.<br>
     *            They may consist of a maximum of 63 characters.
     */
    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Returns whether the service is offered directly by its supplier or by a
     * broker or reseller.
     * 
     * 
     * @return the offering type
     */
    public OfferingType getOfferingType() {
        return offeringType;
    }

    /**
     * Specifies whether the service is offered directly by its supplier or by a
     * broker or reseller.
     * 
     * @param offeringType
     *            the offering type
     */
    public void setOfferingType(OfferingType offeringType) {
        this.offeringType = offeringType;
    }

    /**
     * Returns the URL of an external tool for configuring the service
     * parameters.
     * 
     * @return the URL or <code>null</code> if no configuration tool is used
     */
    public String getConfiguratorUrl() {
        return configuratorUrl;
    }

    /**
     *
     * @return The URL of the custom tab on my subscriptions page
     */
    public String getCustomTabUrl() {
        return customTabUrl;
    }

    /**
     * Sets the URL of an external tool for configuring the service parameters.
     * 
     * @param configuratorUrl
     *            the URL
     */
    public void setConfiguratorUrl(String configuratorUrl) {
        this.configuratorUrl = configuratorUrl;
    }

    public void setCustomTabUrl(String customTabUrl) {
        this.customTabUrl = customTabUrl;
    }

    public String getCustomTabName() {
        return customTabName;
    }

    public void setCustomTabName(String customTabName) {
        this.customTabName = customTabName;
    }
}
