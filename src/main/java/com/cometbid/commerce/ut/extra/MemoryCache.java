package com.cometbid.commerce.ut.extra;

import com.cometbid.commerce.ut.common.DomainObject;
import com.cometbid.commerce.ut.qualifiers.JavaUtilLogger;
import com.cometbid.ut.entities.AccountTypeEO;
import com.cometbid.ut.entities.CountryEO;
import com.cometbid.ut.entities.CurrencyEO;
import com.cometbid.ut.entities.LanguageEO;
import com.cometbid.ut.entities.RegionEO;
import com.cometbid.ut.entities.StateProvEO;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.ejb.DependsOn;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Gbenga
 */
@Singleton
@Startup
@Lock(LockType.READ)
@DependsOn("MongoDBLoaderHelper")
public class MemoryCache {

    public static final int FIFTY_SECONDS = 50000;

    @PersistenceContext(unitName = "COMETBID_UT_PU")
    private EntityManager em;

    @Inject
    @JavaUtilLogger
    private Logger logger;

    private LoadingCache<String, Map<Integer, DomainObject>> utilityDataCache;
    private LoadingCache<Integer, Map<Integer, StateProvEO>> stateProvDataCache;

    /**
     *
     */
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "#############################################");
        logger.log(Level.INFO, "###      Setting up the CACHE INSTANCE    ###");
        logger.log(Level.INFO, "#############################################");

        utilityDataCache = CacheBuilder.newBuilder()
                .refreshAfterWrite(30, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Map<Integer, DomainObject>>() {

                    @Override
                    public Map<Integer, DomainObject> load(final String key) throws Exception {
                        logger.log(Level.INFO, "+++    Loading utility data from database    +++");

                        return loadApplicationData(key);
                    }
                }
                );

        //-----------------------------------------------------------------------------------------------------------
        //-----------------------------------------------------------------------------------------------------------
        stateProvDataCache = CacheBuilder.newBuilder()
                .refreshAfterWrite(30, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, Map<Integer, StateProvEO>>() {

                    @Override
                    public Map<Integer, StateProvEO> load(Integer countryId) throws Exception {

                        Map<Integer, StateProvEO> stateProvDataMap = new HashMap<>();
                        //make the expensive call
                        List<StateProvEO> stateProvList = selectStatesByCountry(countryId);

                        if (stateProvList.isEmpty()) {
                            return stateProvDataMap;
                        }
                        Iterator<StateProvEO> iter = stateProvList.iterator();
                        while (iter.hasNext()) {
                            StateProvEO stateProv = iter.next();

                            stateProvDataMap.put(stateProv.getStateId(), stateProv);
                        }

                        return stateProvDataMap;
                    }
                }
                );

        loadAllDataToCache();
    }

    /**
     *
     * @return
     */
    @Lock(LockType.READ)
    @AccessTimeout(unit = TimeUnit.SECONDS, value = FIFTY_SECONDS)
    public LoadingCache<String, Map<Integer, DomainObject>> getUtilityDataCache() {
        return utilityDataCache;
    }

    /**
     *
     * @param keyString
     * @return
     * @throws ExecutionException
     */
    @Lock(LockType.READ)
    @AccessTimeout(unit = TimeUnit.SECONDS, value = FIFTY_SECONDS)
    public Map<Integer, DomainObject> getUtilityDataMap(String keyString) throws ExecutionException {
        return utilityDataCache.get(keyString);
    }

    @Lock(LockType.READ)
    @AccessTimeout(unit = TimeUnit.SECONDS, value = FIFTY_SECONDS)
    public LoadingCache<Integer, Map<Integer, StateProvEO>> getStateProvDataCache() {
        return stateProvDataCache;
    }

    @Lock(LockType.READ)
    @AccessTimeout(unit = TimeUnit.SECONDS, value = FIFTY_SECONDS)
    public Map<Integer, StateProvEO> getStateProvDataMap(Integer countryId) throws ExecutionException {
        return stateProvDataCache.get(countryId);
    }

    /*
    @Lock(LockType.WRITE)
    @AccessTimeout(unit = TimeUnit.SECONDS, value = TEN_SECONDS)
    public void putInUtilityDataCache(String keyString, Map<Integer, DomainObject> dataMap)
    throws ExecutionException {
    
    utilityDataCache.put(keyString, dataMap);
    }*/
    private void loadAllDataToCache() {

        String[] strArray = {GlobalConstants.acctTypes, GlobalConstants.countries,
            GlobalConstants.currencies, GlobalConstants.languages,
            GlobalConstants.regions, GlobalConstants.stateProvs};

        for (String strValue : strArray) {
            Map<Integer, DomainObject> mapCache = loadApplicationData(strValue);

            if (mapCache != null) {
                utilityDataCache.put(strValue, loadApplicationData(strValue));
            }
        }
    }

    /**
     * Load the application data from the database
     *
     * @param entityTypeStr entity unique key String while accessing from Cache
     */
    private Map<Integer, DomainObject> loadApplicationData(String entityTypeStr) {
        Map<Integer, DomainObject> domainObjectMap = new HashMap<>();

        logger.log(Level.INFO, "Trying to load data from Cache.......++++---------------");

        if (entityTypeStr.equalsIgnoreCase(GlobalConstants.regions)) {
            List<RegionEO> regionList = em.createNamedQuery("RegionEO.findAll").getResultList();

            if (!regionList.isEmpty()) {
                Iterator<RegionEO> iter = regionList.iterator();

                while (iter.hasNext()) {
                    RegionEO aRegion = iter.next();
                    domainObjectMap.put(aRegion.getRegionId(), aRegion);
                }
            } else {

                logger.log(Level.INFO, "No Region record to load into Cache");
            }
        } else if (entityTypeStr.equalsIgnoreCase(GlobalConstants.acctTypes)) {

            List<AccountTypeEO> subscripitionList = em.createNamedQuery("AccountTypeEO.findAll").getResultList();

            if (!subscripitionList.isEmpty()) {
                Iterator<AccountTypeEO> iter = subscripitionList.iterator();

                while (iter.hasNext()) {
                    AccountTypeEO aSubscriptionType = iter.next();
                    domainObjectMap.put(aSubscriptionType.getAcctTypeId(), aSubscriptionType);
                }
            } else {

                logger.log(Level.INFO, "No subscription types record to load into Cache");
            }
        } else if (entityTypeStr.equalsIgnoreCase(GlobalConstants.countries)) {

            List<CountryEO> countryList = em.createNamedQuery("CountryEO.findAll").getResultList();

            if (!countryList.isEmpty()) {
                // domainObjectMap = new HashMap<>();
                Iterator<CountryEO> iter = countryList.iterator();

                while (iter.hasNext()) {
                    CountryEO aCountry = iter.next();
                    domainObjectMap.put(aCountry.getCountryId(), aCountry);
                }
            } else {

                logger.log(Level.INFO, "No country record to load into Cache");
            }
        } else if (entityTypeStr.equalsIgnoreCase(GlobalConstants.currencies)) {

            List<CurrencyEO> currencyList = em.createNamedQuery("CurrencyEO.findAll").getResultList();

            if (!currencyList.isEmpty()) {
                // domainObjectMap = new HashMap<>();
                Iterator<CurrencyEO> iter = currencyList.iterator();

                while (iter.hasNext()) {
                    CurrencyEO aCurrency = iter.next();
                    domainObjectMap.put(aCurrency.getCurrencyId(), aCurrency);
                }
            } else {

                logger.log(Level.INFO, "No currencies record to load into Cache");
            }
        } else if (entityTypeStr.equalsIgnoreCase(GlobalConstants.languages)) {

            List<LanguageEO> languageList = em.createNamedQuery("LanguageEO.findAll").getResultList();

            if (!languageList.isEmpty()) {
                // domainObjectMap = new HashMap<>();
                Iterator<LanguageEO> iter = languageList.iterator();

                while (iter.hasNext()) {
                    LanguageEO aLanguage = iter.next();
                    domainObjectMap.put(aLanguage.getLangId(), aLanguage);
                }
            } else {

                logger.log(Level.INFO, "No languages record to load into Cache");
            }
        }

        return domainObjectMap;
    }

    @Override
    @PreDestroy
    protected void finalize() {
        try {
            utilityDataCache.cleanUp();
            stateProvDataCache.cleanUp();

            super.finalize();
        } catch (Throwable ex) {
            //ignore
        }

    }

    public void invalidateUtilityDataCache() {
        logger.log(Level.INFO, "********************************************************************");
        logger.log(Level.INFO, "***************      Invalidating Utility cache      ****************");
        logger.log(Level.INFO, "********************************************************************");
        utilityDataCache.invalidateAll();
        stateProvDataCache.invalidateAll();
    }

    public List<StateProvEO> selectStatesByCountry(Integer countryId) {

        String query1 = "db.STATE_PROV_TAB.find({'COUNTRY_ID': \'" + countryId + "\'})";
        Query query = em.createNativeQuery(query1, StateProvEO.class);
        List<StateProvEO> results = query.getResultList();
        return results;
    }
}
