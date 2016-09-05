/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: Dirk Bernsau                                                      
 *                                                                              
 *  Creation Date: July 15, 2011                                                      
 *                                                                              
 *  Completion Time: July 15, 2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.serviceprovisioningservice.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jms.JMSException;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.parser.QueryParser;
import org.apache.solr.search.SyntaxError;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.dataservice.bean.IndexMQSender;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Marketplace;
import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.Product;
import org.oscm.domobjects.bridge.ProductClassBridge;
import org.oscm.domobjects.index.IndexReinitRequestMessage;
import org.oscm.i18nservice.bean.LocalizerFacade;
import org.oscm.i18nservice.local.LocalizerServiceLocal;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.serviceprovisioningservice.assembler.ProductAssembler;
import org.oscm.serviceprovisioningservice.local.ProductSearchResult;
import org.oscm.serviceprovisioningservice.local.SearchServiceLocal;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.usergroupservice.bean.UserGroupServiceLocalBean;
import org.oscm.validation.ArgumentValidator;
import org.oscm.internal.intf.SearchService;
import org.oscm.internal.types.enumtypes.PerformanceHint;
import org.oscm.internal.types.enumtypes.Sorting;
import org.oscm.internal.types.exception.InvalidPhraseException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.ListCriteria;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOServiceListResult;

/**
 * 
 * Implementation of search service functionality.
 * 
 * @author Dirk Bernsau
 * 
 */
@Stateless
@Remote(SearchService.class)
@Local(SearchServiceLocal.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class SearchServiceBean implements SearchService, SearchServiceLocal {

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(SearchServiceBean.class);

    @EJB(beanInterface = DataService.class)
    private DataService dm;

    @EJB(beanInterface = LocalizerServiceLocal.class)
    private LocalizerServiceLocal localizer;

    @Inject
    UserGroupServiceLocalBean userGroupService;

    private static String DEFAULT_LOCALE = "en";

    @Override
    public void initIndexForFulltextSearch(final boolean force) {
        IndexReinitRequestMessage msg = new IndexReinitRequestMessage(force);
        IndexMQSender messageSender = getMQSender();
        try {
            messageSender.sendMessage(msg);
        } catch (JMSException e) {
            logger.logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR_SEARCH_INDEX_CREATION_FAILED);
        }
    }

    protected IndexMQSender getMQSender() {
        return new IndexMQSender();
    }

    @Override
    public VOServiceListResult searchServices(String marketplaceId,
            String locale, String searchPhrase) throws InvalidPhraseException,
            ObjectNotFoundException {
        return searchServices(marketplaceId, locale, searchPhrase,
                PerformanceHint.ALL_FIELDS);
    }

    public VOServiceListResult searchServices(String marketplaceId,
            String locale, String searchPhrase, PerformanceHint performanceHint)
            throws InvalidPhraseException, ObjectNotFoundException {

        ArgumentValidator.notEmptyString("marketplaceId", marketplaceId);
        ArgumentValidator.notEmptyString("locale", locale);
        ArgumentValidator.notEmptyString("searchPhrase", searchPhrase);

        // Load marketplace
        loadMarketplace(marketplaceId);

        List<VOService> voList = new ArrayList<VOService>();
        VOServiceListResult listResult = new VOServiceListResult();
        listResult.setResultSize(voList.size());
        listResult.setServices(voList);

        try {
            Session session = getDm().getSession();
            if (session != null) {
                LinkedHashMap<Long, VOService> map = new LinkedHashMap<Long, VOService>();
                FullTextSession fts = Search.getFullTextSession(session);

                // (1) search in actual locale
                org.apache.lucene.search.Query query = getLuceneQuery(
                        LuceneQueryBuilder.getServiceQuery(searchPhrase,
                                locale, DEFAULT_LOCALE, false), marketplaceId,
                        locale);
                searchViaLucene(query, fts, map);

                if (!DEFAULT_LOCALE.equals(locale)) {
                    // (2) search in default locale
                    query = getLuceneQuery(LuceneQueryBuilder.getServiceQuery(
                            searchPhrase, locale, DEFAULT_LOCALE, true),
                            marketplaceId, DEFAULT_LOCALE);
                    searchViaLucene(query, fts, map);
                }

                Set<Long> keySet = new HashSet<Long>(map.keySet());

                if (keySet.size() > 0) {
                    KeyRestrictedListCriteria listCriteria = new KeyRestrictedListCriteria(
                            keySet);
                    listCriteria.setLimit(100);
                    VOServiceListResult services = getServicesByCriteria(
                            marketplaceId, locale, listCriteria,
                            performanceHint);
                    for (VOService svc : services.getServices()) {
                        Long key = Long.valueOf(svc.getKey());
                        if (map.containsKey(key)) {
                            map.put(key, svc);
                        }
                    }
                }
                for (Long key : map.keySet()) {
                    if (map.get(key) != null) {
                        voList.add(map.get(key));
                    }
                }
            }
        } catch (SyntaxError | QueryNodeException e) {
            InvalidPhraseException ipe = new InvalidPhraseException(e,
                    searchPhrase);
            logger.logDebug(ipe.getMessage());
            throw ipe;
        } finally {
            listResult.setResultSize(voList.size());
            listResult.setServices(voList);

        }
        return listResult;
    }

    private Set<Long> getInvisibleServiceKeySet(PlatformUser user)
            throws ObjectNotFoundException {
        if ((user == null) || user.isOrganizationAdmin()) {
            return null;
        }
        List<Long> invisibleKeys = userGroupService
                .getInvisibleProductKeysForUser(user.getKey());
        return new HashSet<Long>(invisibleKeys);
    }

    /**
     * Returns the Lucene query for the given locale and query text.
     * 
     * @param searchString
     *            the text query for the Lucene query parser
     * @param locale
     *            the locale for the analyzer to use
     * @param mId
     *            the marketplace id
     * @return the Lucene query for the given locale and query text
     */
    private org.apache.lucene.search.Query getLuceneQuery(String searchString,
        String mId, String locale) throws SyntaxError, QueryNodeException {

        // use analyzer for actual text part of query
        StandardQueryParser  parser = new StandardQueryParser();
        org.apache.lucene.search.Query textQuery = parser.parse(searchString, ProductClassBridge.TAGS + locale);

        // build mId part (use no analyzer!)
        TermQuery mIdQuery = new TermQuery(new Term(ProductClassBridge.MP_ID,
                QueryParser.escape(mId).toLowerCase()));

        // now construct final query
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        query.add(mIdQuery, Occur.MUST);
        query.add(textQuery, Occur.MUST);
        return query.build();
    }

    /**
     * Performs a search in Lucene and puts the resulting product object ids in
     * a corresponding map.
     * 
     * @param query
     *            the Lucene query
     * @param fts
     *            the Hibernate Search FullTextSession
     * @param map
     *            the map for the search results
     * @throws HibernateException
     */
    private void searchViaLucene(org.apache.lucene.search.Query query,
            FullTextSession fts, LinkedHashMap<Long, VOService> map)
            throws HibernateException {
        FullTextQuery ftQuery = fts.createFullTextQuery(query, Product.class);
        ftQuery.setProjection("key");
        List<?> result = ftQuery.list();
        if (result != null) {
            for (Object item : result) {
                map.put((Long) ((Object[]) item)[0], null);
            }
        }
    }

    @Override
    public VOServiceListResult getServicesByCriteria(String marketplaceId,
            String locale, ListCriteria listCriteria)
            throws ObjectNotFoundException {
        return getServicesByCriteria(marketplaceId, locale, listCriteria,
                PerformanceHint.ALL_FIELDS);
    }

    public VOServiceListResult getServicesByCriteria(String marketplaceId,
            String locale, ListCriteria listCriteria,
            PerformanceHint performanceHint) throws ObjectNotFoundException {
        ArgumentValidator.notEmptyString("marketplaceId", marketplaceId);
        ArgumentValidator.notEmptyString("locale", locale);
        ArgumentValidator.notNull("listCriteria", listCriteria);

        PlatformUser user = getDm().getCurrentUserIfPresent();
        Set<Long> invisibleKeys = getInvisibleServiceKeySet(user);
        ProductSearch search = new ProductSearch(getDm(), marketplaceId,
                listCriteria, DEFAULT_LOCALE, locale, invisibleKeys);

        return convertToVoServiceList(search.execute(), locale, performanceHint);
    }

    public VOServiceListResult getAccesibleServices(
            String marketplaceId, String locale, ListCriteria listCriteria,
            PerformanceHint performanceHint) throws ObjectNotFoundException {
        ArgumentValidator.notEmptyString("marketplaceId", marketplaceId);
        ArgumentValidator.notEmptyString("locale", locale);
        ArgumentValidator.notNull("listCriteria", listCriteria);

        PlatformUser user = getDm().getCurrentUserIfPresent();
        // temporary solution to get all services for initializing the accesible
        // service list for unit admin
        Set<Long> invisibleKeys = null;
        if ((user != null) && !user.isOrganizationAdmin()
                && !user.isUnitAdmin()) {
            List<Long> invisibleKeyList = userGroupService
                    .getInvisibleProductKeysForUser(user.getKey());
            invisibleKeys = new HashSet<Long>(invisibleKeyList);
        }

        ProductSearch search = new ProductSearch(getDm(), marketplaceId,
                listCriteria, DEFAULT_LOCALE, locale, invisibleKeys);

        return convertToVoServiceList(search.execute(), locale, performanceHint);
    }

    VOServiceListResult convertToVoServiceList(ProductSearchResult services,
            String locale, PerformanceHint performanceHint) {
        VOServiceListResult result = new VOServiceListResult();
        LocalizerFacade facade = new LocalizerFacade(localizer, locale);
        result.setResultSize(services.getResultSize());
        result.setServices(convertToVoServices(services.getServices(), facade,
                performanceHint));
        return result;
    }

    /**
     * Converts the product list to transfer objects.
     * 
     * @param productList
     *            List of domain products
     * @return found VO services
     */
    private List<VOService> convertToVoServices(List<Product> productList,
            LocalizerFacade facade, PerformanceHint performanceHint) {
        List<VOService> resultList = new ArrayList<VOService>();
        ProductAssembler.prefetchData(productList, facade, performanceHint);
        for (Product product : productList) {
            resultList.add(ProductAssembler.toVOProduct(product, facade,
                    performanceHint));
        }
        return resultList;
    }

    private Marketplace loadMarketplace(String marketplaceId)
            throws ObjectNotFoundException {
        Marketplace marketplace = new Marketplace(marketplaceId);
        return (Marketplace) getDm().getReferenceByBusinessKey(marketplace);
    }

    @Override
    public ProductSearchResult getServicesByCategory(String marketplaceId,
            String categoryId) throws ObjectNotFoundException {
        ArgumentValidator.notEmptyString("marketplaceId", marketplaceId);
        ArgumentValidator.notEmptyString("categoryId", categoryId);

        PlatformUser user = getDm().getCurrentUserIfPresent();
        Set<Long> invisibleKeys = getInvisibleServiceKeySet(user);

        ProductSearch search = new ProductSearch(getDm(), marketplaceId,
                generateListCriteria(categoryId), DEFAULT_LOCALE, null,
                invisibleKeys);
        return search.execute();
    }

    private ListCriteria generateListCriteria(String categoryId) {
        ListCriteria criteria = new ListCriteria();
        criteria.setSorting(Sorting.ACTIVATION_DESCENDING);
        criteria.setCategoryId(categoryId);
        criteria.setOffset(0);
        criteria.setLimit(100);
        return criteria;
    }

    public DataService getDm() {
        return dm;
    }

    public void setDm(DataService dm) {
        this.dm = dm;
    }

}
