/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.routine;

import com.cometbid.commerce.ut.dto.CallingCode;
import com.cometbid.commerce.ut.dto.DiallingCode;
import com.cometbid.commerce.ut.dto.PhoneFormat;
import com.cometbid.commerce.ut.qualifiers.JavaUtilLogger;
import com.cometbid.commerce.ut.qualifiers.Logged;
import com.cometbid.commerce.ut.qualifiers.MongoDBDataLoader;
import com.cometbid.commerce.ut.rs.extra.JsonToEntityGenerator;
import com.cometbid.commerce.ut.services.AccountTypeFacadeLocal;
import com.cometbid.commerce.ut.services.CountryFacadeLocal;
import com.cometbid.commerce.ut.services.CurrencyFacadeLocal;
import com.cometbid.commerce.ut.services.LanguageFacadeLocal;
import com.cometbid.commerce.ut.services.RegionFacadeLocal;
import com.cometbid.commerce.ut.services.StateProvFacadeLocal;
import com.cometbid.ut.entities.AccountTypeEO;
import com.cometbid.ut.entities.CountryEO;
import com.cometbid.ut.entities.CurrencyEO;
import com.cometbid.ut.entities.LanguageEO;
import com.cometbid.ut.entities.RegionEO;
import com.cometbid.ut.entities.StateProvEO;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Gbenga
 */
@MongoDBDataLoader
@Singleton
@Startup
@Logged
public class MongoDBLoaderHelper {

    @PersistenceContext(unitName = "COMETBID_UT_PU")
    private EntityManager em;

    private Client client;

    @Inject
    private RegionFacadeLocal regionFacade;
    @Inject
    private CurrencyFacadeLocal currencyFacade;
    @Inject
    private CountryFacadeLocal countryFacade;
    @Inject
    private LanguageFacadeLocal languageFacade;
    @Inject
    private AccountTypeFacadeLocal accountTypeFacade;
    @Inject
    private StateProvFacadeLocal statesFacade;

    @Inject
    @JavaUtilLogger
    private Logger logger;

    private Future[] futureObjectArray;
    private boolean dataLoadSuccessful = false;

    @PostConstruct
    public void loadDefaultData() {

        try {

            Util.loadURLProperties();

            initializeConnectionProperties();

            doDataLoading();

            //  processFutureQueues();
        } catch (Exception ex) {
            dataLoadSuccessful = false; //loading remote data failed

            ex.printStackTrace();
            logger.log(Level.SEVERE, "Loading of Data to Staging Tables failed to complete {0}", ex.getMessage());
        }
    }

    private void initializeConnectionProperties() {
        client = ClientBuilder.newClient();

        futureObjectArray = new Future[12];
    }

    private void createFutureObject(int index, String uri) {

        Future<String> futureResponse = client.target(uri)
                .request(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json")
                .async()
                .get(String.class);

        futureObjectArray[index] = futureResponse;
    }

    /**
     *
     *
     */
    // @Override
    public void batchloadSubscriptionTypes() {
        //5. Load the Subscriptions             
        logger.log(Level.INFO, "Connecting remotely to load Subscription types data...");

        createFutureObject(4, Util.subscriptionsUrl);
    }

    /**
     *
     */
    // @Override
    public void batchloadCountries() {
        //3. Load the Countries             
        logger.log(Level.INFO, "Connecting remotely to load Country data...");

        createFutureObject(2, Util.countriesUrl);
    }

    /**
     *
     *
     */
    // @Override
    public void batchloadCurrencies() {
        //2. Load the Currencies             
        logger.log(Level.INFO, "Connecting remotely to load Currency data...");

        createFutureObject(1, Util.currenciesUrl);
    }

    /**
     *
     *
     */
    // @Override
    public void batchloadRegions() {
        //1. Load the Regions             
        logger.log(Level.INFO, "Connecting remotely to load Regions data...");

        createFutureObject(0, Util.regionsUrl);
    }

    /**
     *
     */
    // @Override
    public void batchloadLanguages() {
        //4. Load the Languages             
        logger.log(Level.INFO, "Connecting remotely to load langauges data...");

        createFutureObject(3, Util.languagesUrl);
    }

    /**
     *
     */
    // @Override
    public void batchloadStateProvinces() {
        //1. Load the Currencies             
        logger.log(Level.INFO, "Connecting remotely to load State/Province data...");

        createFutureObject(5, Util.statesProvincesUrl1);

        createFutureObject(6, Util.statesProvincesUrl2);

        createFutureObject(7, Util.statesProvincesUrl3);

        createFutureObject(8, Util.statesProvincesUrl4);

    }

    /**
     *
     */
    // @Override
    public void batchloadCallingCodes() {
        //5. Load the Currencies             
        logger.log(Level.INFO, "Connecting remotely to load data...");

        createFutureObject(9, Util.callingCodesUrl);
    }

    /**
     *
     */
    // @Override
    public void batchloadDiallingCodes() {
        //1. Load the Currencies             
        logger.log(Level.INFO, "Connecting remotely to load data...");

        createFutureObject(10, Util.diallingCodesUrl);
    }

    /**
     *
     */
    // @Override
    public void batchloadPhoneFormats() {
        //1. Load the Currencies             
        logger.log(Level.INFO, "Connecting remotely to load data...");

        createFutureObject(11, Util.phoneFormatsUrl);
    }

    private void doDataLoading() {

        conditionalRegionLoad();

        conditionalCurrenciesLoad();

        conditionalCountriesLoad();

        conditionalLanguagesLoad();

        conditionalAccountTypesLoad();

        conditionalStatesLoad();

    }

    private void conditionalRegionLoad() {
        List<RegionEO> regionList = em.createNamedQuery("RegionEO.findAll")
                .getResultList();

        if (regionList.isEmpty()) {

            em.createNativeQuery("db.REGION_TAB.drop()").executeUpdate();

            //1. Load the Regions 
            batchloadRegions();
        }
    }

    private void conditionalCurrenciesLoad() {

        List<CurrencyEO> currencyList = em.createNamedQuery("CurrencyEO.findAll")
                .getResultList();

        if (currencyList.isEmpty()) {
            logger.log(Level.INFO, "No record found in MongoDB for Currencies...");

            em.createNativeQuery("db.CURRENCY_TAB.drop()").executeUpdate();
            //2. Load the Currencies
            batchloadCurrencies();
        }
    }

    private void conditionalCountriesLoad() {

        List<CountryEO> countryList = em.createNamedQuery("CountryEO.findAll")
                .getResultList();

        if (countryList.isEmpty()) {
            logger.log(Level.INFO, "No record found in MongoDB for Countries...");

            em.createNativeQuery("db.CURRENCY_TAB.drop()").executeUpdate();
            //3. Load the Countries
            batchloadCountries();

            batchloadCallingCodes();

            batchloadDiallingCodes();

            batchloadPhoneFormats();
        }

    }

    private void conditionalLanguagesLoad() {

        List<LanguageEO> languageList = em.createNamedQuery("LanguageEO.findAll")
                .getResultList();

        if (languageList.isEmpty()) {
            logger.log(Level.INFO, "No record found in MongoDB for Languages...");

            em.createNativeQuery("db.LANGUAGE_TAB.drop()").executeUpdate();
            //4. Load the Languages
            batchloadLanguages();
        }
    }

    private void conditionalAccountTypesLoad() {

        List<AccountTypeEO> accountTypes = em.createNamedQuery("AccountTypeEO.findAll")
                .getResultList();

        if (accountTypes.isEmpty()) {
            logger.log(Level.INFO, "No record found in MongoDB for Account types...");

            em.createNativeQuery("db.ACCOUNT_TYPES.drop()").executeUpdate();
            //5. Load the Subscription types
            batchloadSubscriptionTypes();
        }
    }

    private void conditionalStatesLoad() {

        List<StateProvEO> statesList = em.createNamedQuery("StateProvEO.findAll")
                .getResultList();

        if (statesList.isEmpty()) {
            logger.log(Level.INFO, "No record found in MongoDB for States/Provinces types...");

            em.createNativeQuery("db.STATE_PROV_TAB.drop()").executeUpdate();
            //6. Load the State/Province types
            batchloadStateProvinces();
        }
    }

    private void processFutureQueues() throws Exception {

        boolean keepWaiting = true;
        boolean notCancelled = true;

        while (keepWaiting && notCancelled) {
            for (int index = 0; index < futureObjectArray.length; ++index) {
                Future futureObject = futureObjectArray[index];

                if (futureObject != null) {
                    if (futureObject.isCancelled()) {
                        notCancelled = false;

                        logger.log(Level.WARNING, "Cancelled..{0}", index);
                        break;
                    }
                    if (!futureObject.isDone()) {
                        keepWaiting = true;
                        logger.log(Level.WARNING, "Not Done..{0}", index);
                        break;
                    }
                    keepWaiting = false;

                    if (processBatchLoading(keepWaiting, notCancelled)) {
                        dataLoadSuccessful = true;
                        releaseResources();
                        break;
                    }
                    if (keepWaiting && notCancelled) {
                        logger.log(Level.WARNING, "Waiting another 10 seconds for remote calls to return...");
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            logger.log(Level.WARNING, "Waiting thread interrupted...");
                        }
                    }
                }
            }
        }
    }

    private boolean processBatchLoading(boolean keepWaiting, boolean notCancelled) throws Exception {

        boolean dataLoaded = false;
        for (int index = 0; index < futureObjectArray.length && !keepWaiting && notCancelled; ++index) {
            Future futureObject = futureObjectArray[index];

            if (futureObject != null) {
                if (futureObject.isDone()) {
                    // Wait 5 seconds
                    try {
                        Object response = futureObject.get(5, TimeUnit.SECONDS);

                        saveBatchLoad(index, response);
                        dataLoaded = true;
                    } catch (InterruptedException ex) {
                        dataLoaded = false;
                        logger.log(Level.WARNING, "Remote Call to load data has been interrupted...", ex);
                        throw ex;
                    } catch (TimeoutException timeout) {
                        dataLoaded = false;
                        logger.log(Level.WARNING, "Remote Call to load data timed out...", timeout);
                        throw timeout;
                    } catch (ExecutionException ex) {
                        Throwable cause = ex.getCause();

                        if (cause instanceof WebApplicationException) {
                            WebApplicationException wae = (WebApplicationException) cause;
                            wae.getResponse().close();
                        } else if (cause instanceof ResponseProcessingException) {
                            ResponseProcessingException rpe = (ResponseProcessingException) cause;
                            rpe.getResponse().close();
                        } else if (cause instanceof ProcessingException) {
                            // handle processing exception
                            logger.log(Level.WARNING, "Error occured processing Response from Remote Call...", ex);
                        } else {
                            logger.log(Level.WARNING, "An unknown exception occured while loading data to MongoDB...", ex);
                        }
                        dataLoaded = false;
                        throw ex;
                    } catch (Exception ex) {
                        dataLoaded = false;
                        throw ex;
                    }
                }
            }
        }
        return dataLoaded;
    }

    private void releaseResources() {

        logger.log(Level.INFO, "Releasing Connection resources", futureObjectArray);
        for (Future futureObj : futureObjectArray) {
            futureObj = null;
        }

        client.close();

        if (dataLoadSuccessful) {
            client = null;
        }
    }

    private void saveBatchLoad(int index, Object response)
            throws InterruptedException, ExecutionException, TimeoutException {

        switch (index) {
            case 0:
                List<RegionEO> regionList = JsonToEntityGenerator.getInstance()
                        .getRegionListFromJson(response.toString());

                logger.log(Level.INFO, "{0} Regions extracted from JSON", regionList.size());
                regionFacade.bulkLoadRegions(regionList);
                break;
            case 1:
                List<CurrencyEO> currencyList = JsonToEntityGenerator.getInstance()
                        .getCurrencyListFromJson(response.toString());

                logger.log(Level.INFO, "{0} Currencies extracted from JSON", currencyList.size());
                currencyFacade.bulkLoadCurrencies(currencyList);
                break;
            case 2:
                List<CountryEO> countriesLoaded = JsonToEntityGenerator.getInstance()
                        .getCountryListFromJson(response.toString());

                logger.log(Level.INFO, "{0} Countries extracted from JSON", countriesLoaded.size());
                countryFacade.bulkLoadCountries(countriesLoaded);
                break;
            case 3:
                List<LanguageEO> languagesLoaded = JsonToEntityGenerator.getInstance()
                        .getLanguageListFromJson(response.toString());

                logger.log(Level.INFO, "{0} Languages extracted from JSON", languagesLoaded.size());
                languageFacade.bulkLoadLanguages(languagesLoaded);
                break;
            case 4:
                List<AccountTypeEO> subscriptionsLoaded = JsonToEntityGenerator.getInstance()
                        .getSubscriptionListFromJson(response.toString());

                logger.log(Level.INFO, "{0} Subscription types extracted from JSON", subscriptionsLoaded.size());
                accountTypeFacade.bulkLoadAccountTypes(subscriptionsLoaded);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                List<StateProvEO> statesProvLoaded = JsonToEntityGenerator.getInstance()
                        .getStateListFromJson(response.toString());

                logger.log(Level.INFO, "{0} State/Provinces extracted from JSON", statesProvLoaded.size());
                statesFacade.bulkLoadStateProvs(statesProvLoaded);
                break;
            case 9:
                List<CallingCode> callingCodeLoaded = JsonToEntityGenerator.getInstance()
                        .getCallingCodesFromJson(response.toString());

                logger.log(Level.INFO, "{0} Calling Codes extracted from JSON", callingCodeLoaded.size());
                countryFacade.bulkLoadCallingCode(callingCodeLoaded);
                break;
            case 10:
                List<DiallingCode> diallingCodeLoaded = JsonToEntityGenerator.getInstance()
                        .getDiallingCodesFromJson(response.toString());

                logger.log(Level.INFO, "{0} Dialling Codes extracted from JSON", diallingCodeLoaded.size());
                countryFacade.bulkLoadDiallingCode(diallingCodeLoaded);
                break;
            case 11:
                List<PhoneFormat> phoneFormatLoaded = JsonToEntityGenerator.getInstance()
                        .getPhoneFormatsFromJson(response.toString());

                logger.log(Level.INFO, "{0} Phone Formats extracted from JSON", phoneFormatLoaded.size());
                countryFacade.bulkLoadPhoneFormat(phoneFormatLoaded);
                break;
            default:
                logger.log(Level.WARNING, "Array index out of bound...");
        }

    }

    /**
     *
     * @param t
     */
    @Schedule(second = "0", minute = "*", hour = "*",
            dayOfWeek = "*", info = "1 minute interval timer",
            persistent = false)
    public void doPeriodicCacheUpdate(Timer t) {

        logger.log(Level.SEVERE, "{Timer}Remote Call to pull data currently running");
        try {

            if (!dataLoadSuccessful) {

                processFutureQueues();

                if (dataLoadSuccessful) {
                    logger.log(Level.SEVERE, "-----{Timer} Remote Data load is successful-----------------");
                }

            } else {
                t.cancel();

                logger.log(Level.SEVERE, "The timer making remote call to pull data has been cancelled");
            }
        } catch (Exception ex) {
            dataLoadSuccessful = false; //loading remote data failed
            t.cancel();
            // ex.printStackTrace();
            logger.log(Level.SEVERE, "{Timer}Loading of Data to Staging Tables failed to complete", ex);
        }
    }

    public static <T> T getRequest(String uri, Class<T> responseType) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080" + uri);
        return target.request().get(responseType);
    }
}
