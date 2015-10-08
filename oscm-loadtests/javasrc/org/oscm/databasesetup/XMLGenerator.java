/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Creation Date: 19.05.2009                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.databasesetup;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pravi
 * 
 */
public class XMLGenerator extends Generator implements XMLGeneratorConstants {

    /**
     * Prints some data to a file using a BufferedWriter
     */
    public String getData() {

        final Map<String, String> map = new HashMap<String, String>();
        init(map);

        final StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(XML_ROOT_ELEMENT);

        sbuilder.append(NEW_LINE);
        sbuilder.append(OPENING_TAG_DATASET);
        sbuilder.append(NEW_LINE);

        for (int aa = 0; aa < ELEMENTS.length; aa++) {

            final String elementName = ELEMENTS[aa];

            switch (SetupHelper.getInt(elementName)) {

            case INT_PLATFORMUSER:
                setPlatformUser(map, sbuilder);
                break;

            case INT_PRICEMODEL:
                setPriceModel(map, sbuilder);
                break;

            case INT_TECHINCALPRODUCT:
                setTechincalProduct(map, sbuilder);
                break;

            case INT_SUBSCRIPTION:
                setSubscription(map, sbuilder);
                break;

            case INT_LOCALIZEDRESOURCE:
                setLocalizedResource(map, sbuilder);
                break;

            case INT_PRODUCT:
                setProduct(map, sbuilder);
                break;

            default:
                sbuilder.append(getElement(elementName, map));
                sbuilder.append(NEW_LINE);
            }
        }
        sbuilder.append(CLOSING_TAG_DATASET);
        sbuilder.append(NEW_LINE);
        return sbuilder.toString();
    }

    /**
     * 
     * @param type
     * @param map
     * @return
     */

    private String getElement(final String type, final Map<String, String> map) {

        final StringBuilder sb = new StringBuilder();
        switch (SetupHelper.getInt(type)) {

        case INT_PAYMENTINFO:
            sb.append(ELEM_PAYMENTINFO);
            sb.append(getAllAttributeValues(PAYMENT_ATTRIBUTES, map));
            map.put(ATTR_USEDPAYMENT_TKEY, map.get(SERIAL_NUMBER));
            break;

        case INT_ORGANIZATION:
            sb.append(ELEM_ORGANIZATION);
            sb.append(getAllAttributeValues(ORGANIZATION_ATTRIBUTES, map));
            map.put(ORGANIZATION, map.get(SERIAL_NUMBER));
            break;

        case INT_PLATFORMUSER:
            sb.append(ELEM_PLATFORMUSER);
            sb.append(getAllAttributeValues(PLATFORM_USER_ATTRIBUTES, map));
            break;

        case INT_PRODUCT:
            sb.append(ELEM_PRODUCT);
            sb.append(getAllAttributeValues(PRODUCT_ATTRIBUTES, map));
            break;

        case INT_SUBSCRIPTION:
            sb.append(ELEM_SUBSCRIPTION);
            sb.append(getAllAttributeValues(SUBSCRIPTION_ATTRIBUTES, map));
            break;

        case INT_USAGELICENSE:
            sb.append(ELEM_USAGELICENSE);
            sb.append(getAllAttributeValues(USAGE_LICENCE_ATTRIBUTES, map));
            break;

        case INT_PRICEMODEL:
            sb.append(ELEM_PRICEMODEL);
            sb.append(getAllAttributeValues(PRICE_MODEL_ATTRIBUTES, map));
            break;

        case INT_TECHINCALPRODUCT:
            sb.append(ELEM_TECHINCALPRODUCT);
            sb.append(getAllAttributeValues(TECHINICAL_PRODUCT_ATTRIBUTES, map));
            break;

        case INT_EVENT:
            sb.append(ELEM_EVENT);
            map.put(ATTR_EVENTKEY, map.get(ATTR_PRODUCT_TKEY));
            sb.append(getAllAttributeValues(EVENT_ATTRIBUTES, map));
            break;

        case INT_LOCALIZEDRESOURCE:
            sb.append(ELEM_LOCALIZEDRESOURCE);
            sb.append(getAllAttributeValues(LOCALIZED_RESOURCE_ATTRIBUTES, map));
            break;

        case INT_PRICEDEVENT:
            sb.append(ELEM_PRICEDEVENT);
            sb.append(getAllAttributeValues(PRICED_EVENT_ATTRIBUTES, map));
            break;

        default:
            throw new IllegalArgumentException("Unknown element" + type);
        }
        sb.append(CLOSING_ELEMENT_TAG);
        return sb.toString();
    }

    /**
     * 
     * @param attributes
     * @param map
     * @return
     */
    private String getAllAttributeValues(final String[] attributes,
            final Map<String, String> map) {
        final StringBuilder sb = new StringBuilder();
        for (int ii = 0; ii < attributes.length; ii++) {
            String attribute = attributes[ii];
            sb.append(getAttributeWithValue(attribute,
                    getAttributeValues(attribute, map)));
        }
        return sb.toString();
    }

    private void setTechincalProduct(final Map<String, String> map,
            final StringBuilder sbuilder) {
        sbuilder.append(getElement(TECHINCALPRODUCT, map));
        map.put(ATTR_TECHNICALPRODUCT_TKEY, map.get(SERIAL_NUMBER));
        sbuilder.append(NEW_LINE);

    }

    /**
     * @param map
     * @param sbuilder
     */
    private void setLocalizedResource(final Map<String, String> map,
            final StringBuilder sbuilder) {
        final int maxvalue = Integer.parseInt(map
                .get(NUMBER_OF_LOCALIZED_RESOURCE));
        for (int ii = 0; ii < maxvalue; ii++) {
            final int res = ii % 2;
            String locale = res == 0 ? "EN" : "DE";
            map.put("LOCALE", locale);
            sbuilder.append(getElement(LOCALIZEDRESOURCE, map));
            sbuilder.append(NEW_LINE);
        }
    }

    /**
     * @param map
     * @param sbuilder
     */
    private void setSubscription(final Map<String, String> map,
            final StringBuilder sbuilder) {
        map.put(SERIAL_NUMBER, "0");
        map.put(ATTR_PRODUCT_TKEY, "0");
        sbuilder.append(getElement(SUBSCRIPTION, map));
        sbuilder.append(NEW_LINE);
    }

    /**
     * @param map
     * @param sbuilder
     */
    private void setProduct(final Map<String, String> map,
            final StringBuilder sbuilder) {
        final int priceModelElements = Integer.parseInt(map
                .get(NUMBER_OF_PRICE_MODEL));
        for (int ii = 0; ii < priceModelElements; ii++) {
            map.put(SERIAL_NUMBER, "" + ii);
            map.put(ATTR_PRICEMODEL_TKEY,
                    "" + Integer.parseInt(getUserSetting(PRICEMODEL) + ii));
            map.put(PRODUCT, "prod" + ii);
            sbuilder.append(getElement(PRODUCT, map));
            sbuilder.append(NEW_LINE);
        }
    }

    /**
     * @param map
     * @param sbuilder
     * @return
     */
    private void setPriceModel(final Map<String, String> map,
            final StringBuilder sbuilder) {
        final int priceModelElements = Integer.parseInt(map
                .get(NUMBER_OF_PRICE_MODEL));
        for (int ii = 0; ii < priceModelElements; ii++) {
            map.put(SERIAL_NUMBER,
                    "" + Integer.parseInt(getUserSetting(PRICEMODEL) + ii));
            sbuilder.append(getElement(PRICEMODEL, map));
            sbuilder.append(NEW_LINE);
        }
    }

    /**
     * @param map
     * @param sbuilder
     */
    private void setPlatformUser(final Map<String, String> map,
            final StringBuilder sbuilder) {
        final int maxValue = Integer
                .parseInt(getUserSetting(KEY_NUMBER_OF_USER));
        for (int ii = 0; ii < maxValue; ii++) {
            map.put(SERIAL_NUMBER, "" + ii);
            sbuilder.append(getElement(PLATFORMUSER, map));
            sbuilder.append(NEW_LINE);
        }
    }

    /**
     * @param map
     */
    private void init(final Map<String, String> map) {
        map.put(SERIAL_NUMBER, "0");
        map.put(NUMBER_OF_PRICE_MODEL, "5");
        map.put(NUMBER_OF_LOCALIZED_RESOURCE, "2");
    }

    private String getAttributeValues(final String attribute,
            Map<String, String> map) {
        int serialnumber = map.containsKey(SERIAL_NUMBER) ? null == map
                .get(SERIAL_NUMBER) ? 0 : Integer.parseInt(map
                .get(SERIAL_NUMBER)) : 0;
        switch (SetupHelper.getInt(attribute)) {

        case INT_ADMIN:
            return getUserSetting(ATTR_ADMIN);

        case INT_ADDRESS:
            return getUserSetting(ATTR_ADDRESS);

        case INT_ASSIGNMENTDATE:
            return getDate();

        case INT_BASEURL:
            return getUserSetting(KEY_BASEURL);

        case INT_COLLECTIONTYPE:
            return getUserSetting(KEY_COLLECTIONTYPE);

        case INT_CURRENTSTATUS:
            return getUserSetting(ATTR_CURRENTSTATUS);

        case INT_ORGANIZATIONKEY:
            return map.get(ORGANIZATION);

        case INT_CREATIONDATE:
            return getDate();

        case INT_ORGANIZATIONADMIN:
            return "" + serialnumber % 2;

        case INT_ORGANIZATIONID:
            return getUserSetting(ORGANIZATION);

        case INT_DTYPE:
            return getUserSetting(ATTR_DTYPE);

        case INT_EMAIL:
            return getUserSetting(KEY_EMAIL);

        case INT_FAILEDLOGINCOUNTER:
            return "0";

        case INT_IBAN:
            return "DE";

        case INT_KEY:
            return SetupHelper.getTkey(serialnumber);

        case INT_LOCALE:
            return map.get("LOCALE");

        case INT_STATUS:
            return "ACTIVE";

        case INT_SECURITYQUESTION:
            return "NONSENSE";

        case INT_REGISTRATIONDATE:
            return SetupHelper.getDateAsString(getUserSetting(KEY_DATEFORMAT));

        case INT_USEDPAYMENT_TKEY:
            return map.get(ATTR_USEDPAYMENT_TKEY);

        case INT_PRODUCTID:
            return map.get(PRODUCT);

        case INT_PROVISIONING_DATE:
            return SetupHelper.getDateAsString(getUserSetting(KEY_DATEFORMAT));

        case INT_PRICEMODEL_TKEY:
            return map.get(ATTR_PRICEMODEL_TKEY);

        case INT_TECHNICALPRODUCT_TKEY:
            return map.get(ATTR_TECHNICALPRODUCT_TKEY);

        case INT_DESCRIPTION:
            return getUserSetting(ATTR_DESCRIPTION);

        case INT_TECHNICALPRODUCTIDENTIFIER:
            return getUserSetting(ATTR_TECHNICALPRODUCTIDENTIFIER);

        case INT_PRODUCT_TKEY:
            return map.get(ATTR_PRODUCT_TKEY);

        case INT_PRODUCTINSTANCEID:
            return getUserSetting(ATTR_PRODUCTINSTANCEID);

        case INT_PERIOD:
            return getUserSetting(ATTR_PERIOD);

        case INT_PERIODHANDLING:
            return getUserSetting(ATTR_PERIODHANDLING);

        case INT_PRICEPERPERIOD:
            return getUserSetting(ATTR_PRICEPERPERIOD);

        case INT_SUBCRIPTIONID:
            return getUserSetting(ATTR_SUBCRIPTIONID);

        case INT_SUBSCRIPTION_TKEY:
            return getUserSetting(ATTR_SUBSCRIPTION_TKEY);

        case INT_VERSION:
            return getUserSetting(ATTR_VERSION);

        case INT_UNUSED:
            return getUserSetting(ATTR_UNUSED);

        case INT_USER_TKEY:
            return getUserSetting(ATTR_USER_TKEY);

        case INT_USERID:
            return SetupHelper.getUserID(serialnumber,
                    getUserSetting(KEY_USER_PREFIX));

        case INT_EVENTIDENTIFIER:
            return getUserSetting(ATTR_EVENTIDENTIFIER);

        case INT_EVENTPRICE:
            return getUserSetting(ATTR_EVENTPRICE);

        case INT_EVENTKEY:
            return map.get(ATTR_EVENTKEY);

        case INT_OBJECTKEY:
            return map.get(ATTR_EVENTKEY);

        case INT_OBJECTTYPE:
            return getObjectType();

        case INT_VALUE:
            return getDescriptionValue(map);

        case INT_PRICEMODELKEY:
            return map.get(ATTR_PRICEMODEL_TKEY);

        default:
            throw new IllegalArgumentException("Unknown attributes");
        }
    }

    /**
     * @return
     */
    private String getDate() {
        final String dateFormat = getUserSetting(KEY_DATEFORMAT);
        return SetupHelper.getDateAsString(dateFormat);
    }

    /**
     * @param map
     * @return
     */
    private String getDescriptionValue(Map<String, String> map) {
        final String loc = map.get("LOCALE");
        return loc.equals("EN") ? "ENGLISH DESCRIPTION" : "DESTSCH BESCHRIBUNG";
    }

    /**
     * @return
     */
    private String getObjectType() {
        final int remainder = SetupHelper.getRandomNumber(3);
        if (remainder == 0) {
            return "GRADE A";
        } else if (remainder == 1) {
            return "GRADE B";
        } else {
            return "GRADE C";
        }
    }

    private String getAttributeWithValue(final String attribute,
            final String value) {
        final StringBuilder builder = new StringBuilder();
        builder.append(" ");
        builder.append(attribute);
        builder.append("=\"");
        builder.append(value);
        builder.append("\"");
        return builder.toString();
    }

}
