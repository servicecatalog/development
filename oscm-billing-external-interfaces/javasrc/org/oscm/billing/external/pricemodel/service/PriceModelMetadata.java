/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                  
 *                                                                                                                                 
 *  Creation Date: 08.01.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.billing.external.pricemodel.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Price model described with metadata. The metadata is represented with
 * categories and their values.
 * 
 * @TODO replace implementation with Interface & move implementation to
 *       oscm-file-billing-adapter and oscm-portal
 */
public class PriceModelMetadata implements Serializable {

    private static final long serialVersionUID = -6166495584165597917L;

    private UUID id;

    private Map<String, String> metadata = new HashMap<String, String>();

    public PriceModelMetadata(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void put(String metadataCategory, String metadataValue) {
        metadata.put(metadataCategory,
                StringEscapeUtils.unescapeJava(metadataValue));
    }

    public String get(String metadataCategory) {
        return metadata.get(metadataCategory);
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
