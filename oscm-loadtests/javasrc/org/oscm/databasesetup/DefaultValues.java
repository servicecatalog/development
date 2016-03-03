/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: 26.05.2009                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.databasesetup;

public interface DefaultValues extends TestSetupConstants {

    String setupPropertiesFile = "setup.properties";
    String fileName = "data";

    String KEY_PATH = "PATH";
    int INT_PATH = 100000;
    String PATH_VALUE = "javares/";

    String KEY_USER_PREFIX = "USERPREFIX";
    int INT_USER_PREFIX = 100001;
    String DEFAULT_USER_PREFIX = "admin";

    String KEY_PASSWORD = "PASSWORD";
    int INT_DEFAULT_PASSWORD = 100002;
    String DEFAULT_PASSWORD_VALUE = "secret";

    String KEY_CLIENT_NAME = "CLIENTNAME";
    int INT_CLIENT_NAME = 100003;
    String DEFAULT_CLIENT_NAME = "FUJITSU";

    String KEY_DATEFORMAT = "DATEFORMAT";
    int INT_DATE_FORMAT = 100004;
    String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    int INT_NUMBER_USER = 100005;
    String KEY_NUMBER_OF_USER = "NUMBEROFUSER";
    int DEFAULT_NUMBER_USER = 100;

    String KEY_EMAIL = ATTR_EMAIL;
    String DEFAULT_EMAIL = "asm-ue-test@est.fujitsu.com";

    String KEY_BASEURL = ATTR_BASEURL;
    String DEFAULT_BASEURL = "http://esthoffmann:1080/example-service";

    String KEY_PRICEMODEL = ATTR_PRICEMODEL_TKEY;
    String DEFAULT_PRICEMODEL = "2000";

    String KEY_ORGANIZATION = ATTR_ORGANIZATIONKEY;
    String DEFAULT_ORGANIZATION = "000";

    String KEY_VERSION = ATTR_VERSION;
    String DEFAULT_VERSION = "1";

    String KEY_TECHNICALPRODUCT_TKEY = ATTR_TECHNICALPRODUCT_TKEY;
    String DEFAULT_TECHNICALPRODUCT_TKEY = "8888";

    String KEY_USEDPAYMENT_TKEY = ATTR_USEDPAYMENT_TKEY;
    String DEFAULT_USEDPAYMENT_TKEY = "000";

    String KEY_PRODUCT = ATTR_PRODUCTID;
    String DEFAULT_PRODUCT = "99898";

    String KEY_DESCRIPTION = ATTR_DESCRIPTION;
    String DEFAULT_DESCRIPTION = "cosy description";

    String KEY_DTYPE = ATTR_DTYPE;
    String DEFAULT_DTYPE = "PeriodFree";

    String KEY_PERIOD = ATTR_PERIOD;
    String DEFAULT_PERIOD = "MONTH";

    String KEY_PERIODHANDLING = ATTR_PERIODHANDLING;
    String DEFAULT_PERIODHANDLING = "PRORATA";

    String KEY_PRICEPERPERIOD = ATTR_PRICEPERPERIOD;
    String DEFAULT_PRICEPERPERIOD = "123.99";

    String KEY_UNUSED = ATTR_UNUSED;
    String DEFAULT_UNUSED = "0";

    String KEY_TECHNICALPRODUCTIDENTIFIER = ATTR_TECHNICALPRODUCTIDENTIFIER;
    String DEFAULT_TECHNICALPRODUCTIDENTIFIER = "none";

    String KEY_PRODUCTINSTANCEID = ATTR_PRODUCTINSTANCEID;
    String DEFAULT_PRODUCTINSTANCEID = "2002";

    String KEY_SUBCRIPTIONID = ATTR_SUBCRIPTIONID;
    String DEFAULT_SUBCRIPTIONID = "0";

    String KEY_ADMIN = ATTR_ADMIN;
    String DEFAULT_ADMIN = "1";

    String KEY_CURRENTSTATUS = ATTR_CURRENTSTATUS;
    String DEFAULT_CURRENTSTATUS = "ASSIGNED";

    String KEY_SUBSCRIPTION_TKEY = ATTR_SUBSCRIPTION_TKEY;
    String DEFAULT_SUBSCRIPTION_TKEY = "0";

    String KEY_USER_TKEY = ATTR_USER_TKEY;
    String DEFAULT_USER_TKEY = "1";

    String KEY_EVENTPRICE = ATTR_EVENTPRICE;
    String DEFAULT_EVENTPRICE = "1299";

    String KEY_EVENTIDENTIFIER = ATTR_EVENTIDENTIFIER;
    String DEFAULT_EVENTIDENTIFIER = "USER_LOGIN";

    String KEY_COLLECTIONTYPE = ATTR_COLLECTIONTYPE;
    String DEFAULT_COLLECTIONTYPE = "INTERNAL";

    String KEY_ADDRESS = ATTR_ADDRESS;
    String DEFAULT_ADDRESS = "NEW ADDRESS";

}