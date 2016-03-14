/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: Dirk Bernsau                                                      
 *                                                                              
 *  Creation Date: July 12, 2011                                                      
 *                                                                              
 *  Completion Time: September 23, 2011                                           
 *                                                                              
 *******************************************************************************/

package org.oscm.subscriptionservice.search;

import org.apache.lucene.queryParser.QueryParser;
import org.oscm.converter.WhiteSpaceConverter;
import org.oscm.domobjects.bridge.ProductClassBridge;
import org.oscm.internal.types.exception.IllegalArgumentException;

/**
 * @author Dirk Bernsau
 * 
 */
public class LuceneQueryBuilder {

    /**
     * Builds the search query for searching the given search phrase on a
     * certain marketplace.
     * 
     * @param marketplaceId
     *            the id of the marketplace
     * @param locale
     *            the user locale
     * @param searchPhrase
     *            the search phrase
     * @param defaultLocale
     *            if <code>false</code>, check if search term is found in fields
     *            for current locale, otherwise check if fields in current
     *            locale are not specified but in default locale instead
     * @return a Lucene search query
     */
    public static String getSubscriptionQuery(String searchPhrase, String locale,
                                              String defaultLocale, boolean isDefaultLocaleHandling) {
        StringBuffer b = new StringBuffer();

        String phrase = WhiteSpaceConverter.replace(searchPhrase);
        phrase = phrase.trim();

        String[] terms = phrase.split(" ");
        for (int i = 0; i < terms.length; i++) {
            // make sure " " is not set up as mandatory search term, bug 8444
            if (terms[i].trim().length() > 0) {
                if (i > 0) {
                    b.append(" AND ");
                }
                b.append("(");
                b.append(buildClauseForField("dataContainer.purchaseOrderNumber",
                        terms[i], locale, defaultLocale,
                        isDefaultLocaleHandling));
                b.append(" ");
                b.append(buildClauseForField(
                        "dataContainer.subscriptionId", terms[i],
                        locale, defaultLocale, isDefaultLocaleHandling));

                b.append(")");
            }
        }
        return b.toString();
    }

    /**
     * @param locale
     *            the locale
     * @param field
     *            the field to search in
     * @param searchPhrase
     *            the search term
     * @param defaultLocaleMode
     *            if <code>false</code>, check if search term is found in field
     *            for current locale, otherwise check if field in current locale
     *            is not specified but in default locale instead
     */
    private static StringBuffer buildClauseForField(String field,
            String searchPhrase, String locale, String defaultLocale,
            boolean isDefaultLocaleHandling) {
        StringBuffer b = new StringBuffer();
        if (isDefaultLocaleHandling) {
            if (locale.equals(defaultLocale)) {
                throw new IllegalArgumentException(
                        "For default locale handling, locale and default locale must be different");
            }
            b.append("(").append(field)
                    .append(ProductClassBridge.DEFINED_LOCALES_SUFFIX);
            b.append(":(+").append(defaultLocale).append(" -").append(locale);
            b.append(") AND ");
            b.append(field).append(defaultLocale).append(":\"")
                    .append(QueryParser.escape(searchPhrase));
            b.append("\")");
        } else {
            b.append(field);
            b.append(QueryParser.escape(locale)).append(":\"")
                    .append(QueryParser.escape(searchPhrase)).append("\"");
        }
        return b;
    }

}
