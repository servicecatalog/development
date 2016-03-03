/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: 27.05.2009                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.databasesetup;

/**
 * @author pravi
 * 
 */
public interface XMLGeneratorConstants extends TestSetupConstants {

    String NUMBER_OF_PRICE_MODEL = "numberofpriceModel";

    String NUMBER_OF_LOCALIZED_RESOURCE = "numberoflocalizedResource";

    String XML_ROOT_ELEMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";

    String SERIAL_NUMBER = "serialNumber";

    String ELEMENTS[] = { PAYMENTINFO, ORGANIZATION, PLATFORMUSER, PRICEMODEL,
            TECHINCALPRODUCT, PRODUCT, SUBSCRIPTION, USAGELICENSE /*
                                                                   * , EVENT,
                                                                   * PRICEDEVENT
                                                                   * ,
                                                                   * LOCALIZEDRESOURCE
                                                                   */};

    String PAYMENT_ATTRIBUTES[] = { ATTR_KEY, ATTR_IBAN, ATTR_COLLECTIONTYPE,
            ATTR_DTYPE, ATTR_VERSION };

    String ORGANIZATION_ATTRIBUTES[] = { ATTR_KEY, ATTR_VERSION,
            ATTR_REGISTRATIONDATE, ATTR_ADDRESS, ATTR_ORGANIZATIONID,
            ATTR_USEDPAYMENT_TKEY, ATTR_LOCALE };

    String PLATFORM_USER_ATTRIBUTES[] = { ATTR_KEY, ATTR_VERSION,
            ATTR_ORGANIZATIONKEY, ATTR_CREATIONDATE, ATTR_EMAIL, ATTR_USERID,
            ATTR_STATUS, ATTR_ORGANIZATIONADMIN, ATTR_FAILEDLOGINCOUNTER,
            ATTR_SECURITYQUESTION, ATTR_LOCALE };

    String PRODUCT_ATTRIBUTES[] = { ATTR_KEY, ATTR_VERSION, ATTR_PRODUCTID,
            ATTR_PROVISIONING_DATE, ATTR_STATUS, ATTR_PRICEMODEL_TKEY,
            ATTR_TECHNICALPRODUCT_TKEY };

    String SUBSCRIPTION_ATTRIBUTES[] = { ATTR_KEY, ATTR_VERSION,
            ATTR_PRODUCT_TKEY, ATTR_ORGANIZATIONKEY, ATTR_STATUS,
            ATTR_CREATIONDATE, ATTR_SUBCRIPTIONID, ATTR_PRICEMODEL_TKEY,
            ATTR_PRODUCTINSTANCEID };

    String PRICE_MODEL_ATTRIBUTES[] = { ATTR_KEY, ATTR_VERSION,
            ATTR_DESCRIPTION, ATTR_DTYPE, ATTR_UNUSED, ATTR_PERIOD,
            ATTR_PERIODHANDLING, ATTR_PRICEPERPERIOD };

    String TECHINICAL_PRODUCT_ATTRIBUTES[] = { ATTR_KEY, ATTR_VERSION,
            ATTR_BASEURL, ATTR_TECHNICALPRODUCTIDENTIFIER };

    String USAGE_LICENCE_ATTRIBUTES[] = { ATTR_KEY, ATTR_VERSION, ATTR_ADMIN,
            ATTR_ASSIGNMENTDATE, ATTR_CURRENTSTATUS, ATTR_SUBSCRIPTION_TKEY,
            ATTR_USER_TKEY };

    String EVENT_ATTRIBUTES[] = { ATTR_KEY, ATTR_VERSION, ATTR_EVENTIDENTIFIER };

    String PRICED_EVENT_ATTRIBUTES[] = { ATTR_KEY, ATTR_VERSION,
            ATTR_EVENTPRICE, ATTR_EVENTKEY, ATTR_PRICEMODELKEY };

    String LOCALIZED_RESOURCE_ATTRIBUTES[] = { ATTR_LOCALE, ATTR_OBJECTKEY,
            ATTR_OBJECTTYPE, ATTR_VALUE };
}
