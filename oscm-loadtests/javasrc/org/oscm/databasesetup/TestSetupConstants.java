/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: 18.05.2009                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.databasesetup;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pravi
 * 
 */
public interface TestSetupConstants {

    static class MapAttributes {

        private static Map<String, Integer> map = new HashMap<String, Integer>();

        public static int getInt(String key) {
            key = key.toUpperCase();
            if (map.isEmpty()) {
                init();
            }
            return map.get(key).intValue();
        }

        private static void put(String key, int value) {
            map.put(key.toUpperCase(), Integer.valueOf(value));
        }

        private static void init() {

            // Elements to make the the case insensitive
            put(PAYMENTINFO, INT_PAYMENTINFO);
            put(ORGANIZATION, INT_ORGANIZATION);
            put(PLATFORMUSER, INT_PLATFORMUSER);
            put(PRODUCT, INT_PRODUCT);
            put(PRICEMODEL, INT_PRICEMODEL);
            put(SUBSCRIPTION, INT_SUBSCRIPTION);
            put(USAGELICENSE, INT_USAGELICENSE);
            put(TECHINCALPRODUCT, INT_TECHINCALPRODUCT);
            put(EVENT, INT_EVENT);
            put(PRICEDEVENT, INT_PRICEDEVENT);
            put(LOCALIZEDRESOURCE, INT_LOCALIZEDRESOURCE);

            // attributes
            put(ATTR_KEY, INT_KEY);
            put(ATTR_VERSION, INT_VERSION);
            put(ATTR_ORGANIZATIONKEY, INT_ORGANIZATIONKEY);
            put(ATTR_CREATIONDATE, INT_CREATIONDATE);
            put(ATTR_EMAIL, INT_EMAIL);
            put(ATTR_USERID, INT_USERID);
            put(ATTR_STATUS, INT_STATUS);
            put(ATTR_ORGANIZATIONADMIN, INT_ORGANIZATIONADMIN);
            put(ATTR_FAILEDLOGINCOUNTER, INT_FAILEDLOGINCOUNTER);
            put(ATTR_SECURITYQUESTION, INT_SECURITYQUESTION);
            put(ATTR_LOCALE, INT_LOCALE);
            put(ATTR_IBAN, INT_IBAN);
            put(ATTR_COLLECTIONTYPE, INT_COLLECTIONTYPE);
            put(ATTR_DTYPE, INT_DTYPE);
            put(ATTR_REGISTRATIONDATE, INT_REGISTRATIONDATE);
            put(ATTR_ADDRESS, INT_ADDRESS);
            put(ATTR_ORGANIZATIONID, INT_ORGANIZATIONID);
            put(ATTR_USEDPAYMENT_TKEY, INT_USEDPAYMENT_TKEY);
            put(ATTR_PRODUCTID, INT_PRODUCTID);
            put(ATTR_PROVISIONING_DATE, INT_PROVISIONING_DATE);
            put(ATTR_PRICEMODEL_TKEY, INT_PRICEMODEL_TKEY);
            put(ATTR_TECHNICALPRODUCT_TKEY, INT_TECHNICALPRODUCT_TKEY);
            put(ATTR_DESCRIPTION, INT_DESCRIPTION);
            put(ATTR_UNUSED, INT_UNUSED);
            put(ATTR_PERIOD, INT_PERIOD);
            put(ATTR_PERIODHANDLING, INT_PERIODHANDLING);
            put(ATTR_PRICEPERPERIOD, INT_PRICEPERPERIOD);
            put(ATTR_BASEURL, INT_BASEURL);
            put(ATTR_TECHNICALPRODUCTIDENTIFIER, INT_TECHNICALPRODUCTIDENTIFIER);
            put(ATTR_PRODUCT_TKEY, INT_PRODUCT_TKEY);
            put(ATTR_PRODUCTINSTANCEID, INT_PRODUCTINSTANCEID);
            put(ATTR_SUBCRIPTIONID, INT_SUBCRIPTIONID);
            put(ATTR_ADMIN, INT_ADMIN);
            put(ATTR_ASSIGNMENTDATE, INT_ASSIGNMENTDATE);
            put(ATTR_CURRENTSTATUS, INT_CURRENTSTATUS);
            put(ATTR_SUBSCRIPTION_TKEY, INT_SUBSCRIPTION_TKEY);
            put(ATTR_USER_TKEY, INT_USER_TKEY);
            put(ATTR_OBJECTKEY, INT_OBJECTKEY);
            put(ATTR_OBJECTTYPE, INT_OBJECTTYPE);
            put(ATTR_VALUE, INT_VALUE);
            put(ATTR_EVENTIDENTIFIER, INT_EVENTIDENTIFIER);
            put(ATTR_EVENTPRICE, INT_EVENTPRICE);
            put(ATTR_EVENTKEY, INT_EVENTKEY);
            put(ATTR_PRICEMODELKEY, INT_PRICEMODELKEY);

            // configurable data
            put(Generator.KEY_PATH, Generator.INT_PATH);
            put(Generator.KEY_USER_PREFIX, Generator.INT_USER_PREFIX);
            put(Generator.KEY_PASSWORD, Generator.INT_DEFAULT_PASSWORD);
            put(Generator.KEY_CLIENT_NAME, Generator.INT_CLIENT_NAME);
            put(Generator.KEY_DATEFORMAT, Generator.INT_DATE_FORMAT);
            put(Generator.KEY_NUMBER_OF_USER, Generator.INT_NUMBER_USER);
        }
    }

    String BASE_PATH = "javares/";

    String NEW_LINE = "\n";
    int CLIENTID = 0;
    int USER = 1;

    int PASSWORD = 2;
    // XML Attributes
    String OPENING_ELEMENT_TAG = "<";

    String CLOSING_ELEMENT_TAG = "/>";
    String OPENING_TAG_DATASET = "<dataset>";

    // make a not to declare the value in the upper case in case its should in
    // lower case
    // convert to uppercase before adding to the int map

    String CLOSING_TAG_DATASET = "</dataset>";
    String PAYMENTINFO = "PaymentInfo";
    String ELEM_PAYMENTINFO = OPENING_ELEMENT_TAG + "PaymentInfo";

    int INT_PAYMENTINFO = 1001;
    String ORGANIZATION = "Organization";
    String ELEM_ORGANIZATION = OPENING_ELEMENT_TAG + "Organization";

    int INT_ORGANIZATION = 1002;
    String PLATFORMUSER = "PlatformUser";
    String ELEM_PLATFORMUSER = OPENING_ELEMENT_TAG + "PlatformUser";

    int INT_PLATFORMUSER = 1003;
    String PRICEMODEL = "PriceModel";
    String ELEM_PRICEMODEL = OPENING_ELEMENT_TAG + "PriceModel";

    int INT_PRICEMODEL = 1004;
    String TECHINCALPRODUCT = "TechnicalProduct";
    String ELEM_TECHINCALPRODUCT = OPENING_ELEMENT_TAG + "TechnicalProduct";

    int INT_TECHINCALPRODUCT = 1005;
    String PRODUCT = "Product";
    String ELEM_PRODUCT = OPENING_ELEMENT_TAG + "Product";

    int INT_PRODUCT = 1006;
    String SUBSCRIPTION = "Subscription";
    String ELEM_SUBSCRIPTION = OPENING_ELEMENT_TAG + "Subscription";

    int INT_SUBSCRIPTION = 1007;
    String USAGELICENSE = "UsageLicense";
    String ELEM_USAGELICENSE = OPENING_ELEMENT_TAG + "UsageLicense";

    int INT_USAGELICENSE = 1008;
    String EVENT = "Event";
    String ELEM_EVENT = OPENING_ELEMENT_TAG + EVENT;

    int INT_EVENT = 1009;
    String PRICEDEVENT = "PricedEvent";
    String ELEM_PRICEDEVENT = OPENING_ELEMENT_TAG + PRICEDEVENT;

    int INT_PRICEDEVENT = 1010;
    String LOCALIZEDRESOURCE = "LocalizedResource";
    String ELEM_LOCALIZEDRESOURCE = OPENING_ELEMENT_TAG + LOCALIZEDRESOURCE;

    int INT_LOCALIZEDRESOURCE = 1011;
    String ATTR_KEY = "TKEY";

    int INT_KEY = 1;
    String ATTR_VERSION = "VERSION";

    int INT_VERSION = 2;
    String ATTR_ORGANIZATIONKEY = "ORGANIZATIONKEY";

    int INT_ORGANIZATIONKEY = 3;
    String ATTR_CREATIONDATE = "CREATIONDATE";

    int INT_CREATIONDATE = 4;
    String ATTR_EMAIL = "EMAIL";

    int INT_EMAIL = 5;
    String ATTR_USERID = "USERID";

    int INT_USERID = 6;
    String ATTR_STATUS = "STATUS";

    int INT_STATUS = 7;
    String ATTR_ORGANIZATIONADMIN = "ORGANIZATIONADMIN";

    int INT_ORGANIZATIONADMIN = 8;
    String ATTR_FAILEDLOGINCOUNTER = "FAILEDLOGINCOUNTER";

    int INT_FAILEDLOGINCOUNTER = 9;
    String ATTR_SECURITYQUESTION = "SECURITYQUESTION";

    int INT_SECURITYQUESTION = 10;
    String ATTR_LOCALE = "LOCALE";

    int INT_LOCALE = 11;
    String ATTR_IBAN = "IBAN";

    int INT_IBAN = 12;
    String ATTR_COLLECTIONTYPE = "COLLECTIONTYPE";

    int INT_COLLECTIONTYPE = 13;
    String ATTR_DTYPE = "DTYPE";

    int INT_DTYPE = 14;
    String ATTR_REGISTRATIONDATE = "REGISTRATIONDATE";

    int INT_REGISTRATIONDATE = 15;
    String ATTR_ADDRESS = "ADDRESS";

    int INT_ADDRESS = 16;
    String ATTR_ORGANIZATIONID = "ORGANIZATIONID";

    int INT_ORGANIZATIONID = 17;
    String ATTR_USEDPAYMENT_TKEY = "USEDPAYMENT_TKEY";

    int INT_USEDPAYMENT_TKEY = 18;
    String ATTR_PRODUCTID = "PRODUCTID";

    int INT_PRODUCTID = 19;
    String ATTR_PROVISIONING_DATE = "PROVISIONINGDATE";

    int INT_PROVISIONING_DATE = 20;
    String ATTR_PRICEMODEL_TKEY = "PRICEMODEL_TKEY";

    int INT_PRICEMODEL_TKEY = 21;
    String ATTR_TECHNICALPRODUCT_TKEY = "TECHNICALPRODUCT_TKEY";

    int INT_TECHNICALPRODUCT_TKEY = 22;
    String ATTR_DESCRIPTION = "DESCRIPTION";

    int INT_DESCRIPTION = 23;
    String ATTR_UNUSED = "UNUSED";

    int INT_UNUSED = 25;
    String ATTR_PERIOD = "PERIOD";

    int INT_PERIOD = 26;
    String ATTR_PERIODHANDLING = "PERIODHANDLING";

    int INT_PERIODHANDLING = 27;
    String ATTR_PRICEPERPERIOD = "PRICEPERPERIOD";

    int INT_PRICEPERPERIOD = 28;
    String ATTR_BASEURL = "BASEURL";

    int INT_BASEURL = 29;
    String ATTR_TECHNICALPRODUCTIDENTIFIER = "TECHNICALPRODUCTIDENTIFIER";

    int INT_TECHNICALPRODUCTIDENTIFIER = 30;
    String ATTR_PRODUCT_TKEY = "PRODUCT_TKEY";

    int INT_PRODUCT_TKEY = 31;
    String ATTR_PRODUCTINSTANCEID = "PRODUCTINSTANCEID";

    int INT_PRODUCTINSTANCEID = 33;
    String ATTR_SUBCRIPTIONID = "SUBSCRIPTIONID";

    int INT_SUBCRIPTIONID = 35;
    String ATTR_ADMIN = "ADMIN";

    int INT_ADMIN = 36;
    String ATTR_ASSIGNMENTDATE = "ASSIGNMENTDATE";

    int INT_ASSIGNMENTDATE = 37;
    String ATTR_CURRENTSTATUS = "CURRENTSTATUS";

    int INT_CURRENTSTATUS = 38;
    String ATTR_SUBSCRIPTION_TKEY = "SUBSCRIPTION_TKEY";

    int INT_SUBSCRIPTION_TKEY = 39;
    String ATTR_USER_TKEY = "USER_TKEY";

    int INT_USER_TKEY = 40;
    String ATTR_OBJECTKEY = "OBJECTKEY";

    int INT_OBJECTKEY = 41;
    String ATTR_OBJECTTYPE = "OBJECTTYPE";

    int INT_OBJECTTYPE = 42;
    String ATTR_VALUE = "VALUE";

    int INT_VALUE = 43;
    String ATTR_EVENTIDENTIFIER = "EVENTIDENTIFIER";

    int INT_EVENTIDENTIFIER = 44;
    String ATTR_EVENTPRICE = "EVENTPRICE";

    int INT_EVENTPRICE = 45;
    String ATTR_EVENTKEY = "EVENTKEY";

    int INT_EVENTKEY = 46;
    String ATTR_PRICEMODELKEY = "PRICEMODELKEY";

    int INT_PRICEMODELKEY = 47;

}
