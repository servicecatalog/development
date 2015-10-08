/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                
 *                                                                                                                                 
 *  Creation Date: 08.01.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.billing.external.pricemodel.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A list of price models described with their metadata.
 * 
 * @TODO replace implementation with Interface & move implementation to
 *       oscm-file-billing-adapter and oscm-portal
 */
public class PriceModelList implements Serializable {

    private static final long serialVersionUID = -2680222051153784929L;

    private List<String> metadataCategories = new ArrayList<String>();
    private List<PriceModelMetadata> metadata = new ArrayList<PriceModelMetadata>();

    public List<String> getMetadataCategories() {
        return metadataCategories;
    }

    public void setMetadataCategories(List<String> metadataCategories) {
        this.metadataCategories = metadataCategories;
    }

    public List<PriceModelMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<PriceModelMetadata> metadata) {
        this.metadata = metadata;
    }
}
