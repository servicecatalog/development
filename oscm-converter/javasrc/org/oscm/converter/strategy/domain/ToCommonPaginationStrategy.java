/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2015                                           
 *                                                                                                                                  
 *  Creation Date: 23.07.15 14:32
 *
 *******************************************************************************/

package org.oscm.converter.strategy.domain;

import org.oscm.converter.strategy.ConversionStrategy;
import org.oscm.pagination.Pagination;

public class ToCommonPaginationStrategy
        implements
        ConversionStrategy<Pagination, org.oscm.pagination.Pagination> {

    @Override
    public org.oscm.pagination.Pagination convert(
            Pagination pagination) {

        if (pagination == null) {
            return null;
        }

        org.oscm.pagination.Pagination result = new org.oscm.pagination.Pagination();

        result.setLimit(pagination.getLimit());
        result.setOffset(pagination.getOffset());

        return result;
    }
}
