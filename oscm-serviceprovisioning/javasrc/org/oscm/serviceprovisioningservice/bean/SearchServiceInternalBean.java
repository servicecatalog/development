/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: cheld                                                   
 *                                                                              
 *  Creation Date: 19.10.2011                                                      
 *                                                                              
 *  Completion Time: 19.10.2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.serviceprovisioningservice.bean;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.intf.SearchServiceInternal;
import org.oscm.internal.types.enumtypes.PerformanceHint;
import org.oscm.internal.types.exception.InvalidPhraseException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.ListCriteria;
import org.oscm.internal.vo.VOServiceListResult;

/**
 * 
 * Implementation of performance optimized search service functionality. This
 * class contains methods that provide performance optimized access. It is
 * intended only as a temporary optimization and will be replaced by other
 * means. Only for internal usage.
 * 
 * @author cheld
 * 
 */
@Stateless
@Remote(SearchServiceInternal.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class SearchServiceInternalBean extends SearchServiceBean implements
        SearchServiceInternal {

    @Override
    public VOServiceListResult getServicesByCriteria(String marketplaceId,
            String locale, ListCriteria listCriteria,
            PerformanceHint performanceHint) throws ObjectNotFoundException {
        return super.getServicesByCriteria(marketplaceId, locale, listCriteria,
                performanceHint);
    }

    @Override
    public VOServiceListResult getAccesibleServices(
            String marketplaceId, String locale, ListCriteria listCriteria,
            PerformanceHint performanceHint) throws ObjectNotFoundException {
        return super.getAccesibleServices(marketplaceId, locale,
                listCriteria, performanceHint);
    }

    @Override
    public VOServiceListResult searchServices(String marketplaceId,
            String locale, String searchPhrase, PerformanceHint performanceHint)
            throws InvalidPhraseException, ObjectNotFoundException {
        return super.searchServices(marketplaceId, locale, searchPhrase,
                performanceHint);
    }

}
