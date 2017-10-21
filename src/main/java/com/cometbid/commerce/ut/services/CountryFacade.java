/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.services;

import com.cometbid.commerce.ut.cdi.HitCounterInterceptor;
import com.cometbid.commerce.ut.cdi.TimeInMethodInterceptor;
import com.cometbid.commerce.ut.cdi.ValidationInterceptor;
import com.cometbid.commerce.ut.common.BatchUploadFacade;
import com.cometbid.commerce.ut.common.DomainObject;
import com.cometbid.commerce.ut.dto.CallingCode;
import com.cometbid.commerce.ut.dto.DiallingCode;
import com.cometbid.commerce.ut.dto.PhoneFormat;
import com.cometbid.commerce.ut.extra.GlobalConstants;
import com.cometbid.commerce.ut.extra.MemoryCache;
import com.cometbid.commerce.ut.qualifiers.JavaUtilLogger;
import com.cometbid.commerce.ut.qualifiers.Logged;
import com.cometbid.ut.entities.CountryEO;
import com.cometbid.ut.entities.CurrencyEO;
import com.cometbid.ut.entities.RegionEO;
import com.cometbid.ut.entities.StateProvEO;
import com.cometbid.ut.exceptions.CountryNotFoundException;
import com.jcabi.aspects.RetryOnFailure;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import static javax.ejb.LockType.READ;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.DependsOn;
import javax.ejb.EJBException;
import static javax.ejb.LockType.WRITE;
import javax.persistence.OptimisticLockException;

/**
 *
 * @author Gbenga
 */


@Singleton
@ApplicationScoped
@Startup
@Logged
@DependsOn("MemoryCache")
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 1, unit = TimeUnit.MINUTES)
//@Interceptors({HitCounterInterceptor.class, TimeInMethodInterceptor.class})
public class CountryFacade extends BatchUploadFacade<CountryEO> implements CountryFacadeLocal {

    @PersistenceContext(unitName = "COMETBID_UT_PU")
    private EntityManager em;

    @Inject
    private MemoryCache memCache;
    @Inject
    private RegionFacadeLocal regionFacade;

    @Inject
    @JavaUtilLogger
    private Logger logger;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CountryFacade() {
        super(CountryEO.class);
    }

    /**
     *
     * @return @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public Map<Integer, Collection<DomainObject>> getCountriesWithCount() throws CountryNotFoundException {

        try {
            Collection<DomainObject> countries = memCache.getUtilityDataMap(GlobalConstants.countries).values();

            if (countries.isEmpty()) {
                logger.log(Level.SEVERE, "No Country record found");

                throw new CountryNotFoundException(
                        new StringBuilder(100)
                                .append("No Country record found")
                                .toString());
            }
            Map<Integer, Collection<DomainObject>> mapCountRecord = new HashMap<>();
            mapCountRecord.put(countries.size(), countries);

            return Collections.unmodifiableMap(mapCountRecord);

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Countries: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Countries", ex);
        }
    }

    /**
     *
     * @return @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public Collection<DomainObject> getCountriesWithoutCount() throws CountryNotFoundException {

        try {
            Collection<DomainObject> countries = memCache.getUtilityDataMap(GlobalConstants.countries).values();

            if (countries.isEmpty()) {
                logger.log(Level.SEVERE, "No Country record found");

                throw new CountryNotFoundException(
                        new StringBuilder(100)
                                .append("No Country record found")
                                .toString());
            }

            return Collections.unmodifiableCollection(countries);
        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Countries: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Countries", ex);
        }
    }

    /**
     *
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public Map<Integer, Collection<DomainObject>> getCountriesPaginated(Integer pageNumber, Integer pageSize)
            throws CountryNotFoundException {

        try {
            Collection<DomainObject> countryCollection = memCache.getUtilityDataMap(GlobalConstants.countries).values();

            if (countryCollection.isEmpty()) {
                logger.log(Level.SEVERE, "No Country record found");

                throw new CountryNotFoundException(
                        new StringBuilder(100)
                                .append("No Country record found")
                                .toString());
            }

            List<DomainObject> countryList = new ArrayList<>(countryCollection);
            int fromIndex = (pageNumber - 1) * pageSize;
            int toIndex = (pageNumber * (pageSize - 1)) + pageNumber;

            Map<Integer, Collection<DomainObject>> mapCountRecord = new HashMap<>();
            mapCountRecord.put(countryList.size(), countryList.subList(fromIndex, toIndex));

            return Collections.unmodifiableMap(mapCountRecord);

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Countries: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Countries", ex);
        }
    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public CountryEO getCountryById(Integer countryId) throws CountryNotFoundException {

        try {
            DomainObject country = memCache.getUtilityDataMap(GlobalConstants.countries).get(countryId);

            if (country == null) {
                logger.log(Level.SEVERE, "Country with Id: {0} not found", countryId);

                throw new CountryNotFoundException(
                        new StringBuilder(100)
                                .append("Country with Id: ")
                                .append(countryId)
                                .append(" not found")
                                .toString());
            }

            return (CountryEO) country;
        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Countries: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Countries", ex);
        }
    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public int getCountryCodeById(Integer countryId) throws CountryNotFoundException {

        return getCountryById(countryId).getCountryCode();

    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public String getCountryNameById(Integer countryId) throws CountryNotFoundException {

        return getCountryById(countryId).getCountry();

    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public String getCountryCapitalCity(Integer countryId) throws CountryNotFoundException {

        return getCountryById(countryId).getCapitalCity();

    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public long getCountryPopulation(Integer countryId) throws CountryNotFoundException {

        return getCountryById(countryId).getPopulation();

    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public RegionEO getCountryRegion(Integer countryId) throws CountryNotFoundException {

        return getCountryById(countryId).getRegion();

    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public String getAlphaTwoLetterCode(Integer countryId) throws CountryNotFoundException {

        return getCountryById(countryId).getAlpha2Code();

    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public String getAlphaThreeLetterCode(Integer countryId) throws CountryNotFoundException {

        return getCountryById(countryId).getAlpha3Code();

    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public CurrencyEO getCountryStandardCurrency(Integer countryId) throws CountryNotFoundException {

        return getCountryById(countryId).getStandardCurrency();
    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public Set<String> getCountryCallingCodes(Integer countryId) throws CountryNotFoundException {

        Set<String> callingCodes = getCountryById(countryId).getCallingCodes();

        return Collections.unmodifiableSet(callingCodes);

    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public Set<String> getCountryDiallingCodes(Integer countryId) throws CountryNotFoundException {

        Set<String> diallingCodes = getCountryById(countryId).getDiallingCodes();

        return Collections.unmodifiableSet(diallingCodes);

    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public Set<Integer> getCountryPhoneFormats(Integer countryId) throws CountryNotFoundException {

        Set<Integer> phoneFormats = getCountryById(countryId).getPhoneFormats();

        return Collections.unmodifiableSet(phoneFormats);

    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public List<StateProvEO> getCountryStateList(Integer countryId) throws CountryNotFoundException {

        List<StateProvEO> stateProvList = getCountryById(countryId).getStateProvList();

        return Collections.unmodifiableList(stateProvList);

    }

    /**
     *
     * @param countryId
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public Map<Integer, Collection<StateProvEO>> getCountryStateListPaginated(Integer countryId,
            Integer pageNumber, Integer pageSize) throws CountryNotFoundException {

        List<StateProvEO> stateList = getCountryById(countryId).getStateProvList();

        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = (pageNumber * (pageSize - 1)) + pageNumber;

        Map<Integer, Collection<StateProvEO>> mapCountRecord = new HashMap<>();
        mapCountRecord.put(stateList.size(), stateList.subList(fromIndex, toIndex));

        return Collections.unmodifiableMap(mapCountRecord);
    }

    /**
     *
     * @param newCountry
     * @return
     */
    @Lock(WRITE)
    @Override
    @Interceptors(ValidationInterceptor.class)
    public CountryEO addCountry(CountryEO newCountry) {

        try {
            super.create(newCountry);

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while creating new Country with code: {0}",
                    newCountry.getCountryCode());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while creating new Country with code: ")
                            .append(newCountry.getCountryCode())
                            .toString(), exp);
        }

        logger.log(Level.INFO, "Country with id: {0} created successfully.", newCountry.getCountryId());
        return newCountry;
    }

    /**
     *
     * @param updatedCountry
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(WRITE)
    @Override
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public CountryEO editCountry(CountryEO updatedCountry)
            throws CountryNotFoundException {

        CountryEO managedCountry = super.find(updatedCountry.getCountryId());
        if (managedCountry == null) {
            throw new CountryNotFoundException(
                    new StringBuilder(100)
                            .append("Country with Id: ")
                            .append(updatedCountry.getCountryId())
                            .append(" not found")
                            .toString());
        }

        try {

            doCountryDataTransfer(managedCountry, updatedCountry);

            super.edit(managedCountry);
        } catch (OptimisticLockException ex) {
            throw ex;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while editing Country with id: {0}",
                    managedCountry.getCountryId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while editing Country with id: ")
                            .append(managedCountry.getCountryId())
                            .toString(), exp);
        }

        logger.log(Level.INFO, "Country with id {0} updated successfully.", managedCountry.getCountryId());

        return managedCountry;
    }

    private void doCountryDataTransfer(CountryEO updatableCountry, CountryEO updatedCountry) {
        updatableCountry.setAlpha2Code(updatedCountry.getAlpha2Code());
        updatableCountry.setAlpha3Code(updatedCountry.getAlpha3Code());
        updatableCountry.setCapitalCity(updatedCountry.getCapitalCity());
        updatableCountry.setCountry(updatedCountry.getCountry());

        updatableCountry.setCountryCode(updatedCountry.getCountryCode());
        updatableCountry.setDescription(updatedCountry.getDescription());
        updatableCountry.setPopulation(updatedCountry.getPopulation());
        // updatableCountry.setVersion(updatedCountry.getVersion());
        updatableCountry.setStandardCurrency(updatedCountry.getStandardCurrency());
    }

    /**
     *
     * @param countryId
     * @throws CountryNotFoundException
     */
    @Lock(WRITE)
    @Override
    public void removeCountry(Integer countryId) throws CountryNotFoundException {

        CountryEO foundCountry = getCountryById(countryId);
        if (foundCountry == null) {
            throw new CountryNotFoundException(
                    new StringBuilder(100)
                            .append("Country to remove not found by id: ")
                            .append(countryId)
                            .toString());
        }

        try {
            super.remove(foundCountry);

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while editing Country with id: {0}",
                    foundCountry.getCountryId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while editing Country with id: ")
                            .append(foundCountry.getCountryId())
                            .toString(), exp);
        }

        logger.log(Level.INFO, "Country with id {0} is removed successfully.", foundCountry.getCountryId());
    }

    /**
     *
     * @param countryId
     * @param callingCode
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(WRITE)
    @Override
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public CountryEO addCallingCodeToCountry(Integer countryId, String callingCode) throws CountryNotFoundException {

        CountryEO managedCountry = super.find(countryId);
        if (managedCountry == null) {
            throw new CountryNotFoundException(
                    new StringBuilder(100)
                            .append("Country with Id: ")
                            .append(countryId)
                            .append(" not found")
                            .toString());
        }

        try {
            managedCountry.addCallingCode(callingCode);

            super.edit(managedCountry);
        } catch (OptimisticLockException ex) {
            throw ex;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while adding Calling code to Country with id: {0}",
                    managedCountry.getCountryId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while adding Calling code to Country with id: ")
                            .append(managedCountry.getCountryId())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "Country with id {0} updated successfully.", managedCountry.getCountryId());

        return managedCountry;
    }

    /**
     *
     * @param countryId
     * @param callingCode
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(WRITE)
    @Override
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public CountryEO removeCallingCodeFromCountry(Integer countryId, String callingCode) throws CountryNotFoundException {

        CountryEO managedCountry = super.find(countryId);
        if (managedCountry == null) {
            throw new CountryNotFoundException(
                    new StringBuilder(100)
                            .append("Country with Id: ")
                            .append(countryId)
                            .append(" not found")
                            .toString());
        }

        try {
            managedCountry.removeCallingCode(callingCode);

            super.edit(managedCountry);

        } catch (OptimisticLockException ex) {
            throw ex;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while removing Calling code from Country with id: {0}",
                    managedCountry.getCountryId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while removing Calling code from Country with id: ")
                            .append(managedCountry.getCountryId())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "Country with id {0} updated successfully.", managedCountry.getCountryId());

        return managedCountry;
    }

    /**
     *
     * @param countryId
     * @param phoneFormat
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(WRITE)
    @Override
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public CountryEO addPhoneFormatToCountry(Integer countryId, Integer phoneFormat) throws CountryNotFoundException {

        CountryEO managedCountry = super.find(countryId);
        if (managedCountry == null) {
            throw new CountryNotFoundException(
                    new StringBuilder(100)
                            .append("Country with Id: ")
                            .append(countryId)
                            .append(" not found")
                            .toString());
        }

        try {
            managedCountry.addPhoneFormat(phoneFormat);

            super.edit(managedCountry);
        } catch (OptimisticLockException ex) {
            throw ex;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while adding phone format to Country with id: {0}",
                    managedCountry.getCountryId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while adding phone format to Country with id: ")
                            .append(managedCountry.getCountryId())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "Country with id {0} updated successfully.", managedCountry.getCountryId());

        return managedCountry;
    }

    /**
     *
     * @param countryId
     * @param phoneFormat
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(WRITE)
    @Override
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public CountryEO removePhoneFormatFromCountry(Integer countryId, Integer phoneFormat) throws CountryNotFoundException {

        CountryEO managedCountry = super.find(countryId);
        if (managedCountry == null) {
            throw new CountryNotFoundException(
                    new StringBuilder(100)
                            .append("Country with Id: ")
                            .append(countryId)
                            .append(" not found")
                            .toString());
        }

        try {
            managedCountry.removePhoneFormat(phoneFormat);

            super.edit(managedCountry);
        } catch (OptimisticLockException ex) {
            throw ex;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while removing phone Format from Country with id: {0}",
                    managedCountry.getCountryId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while removing phone Format from Country with id: ")
                            .append(managedCountry.getCountryId())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "Country with id {0} updated successfully.", managedCountry.getCountryId());

        return managedCountry;
    }

    /**
     *
     * @param countryId
     * @param diallingCode
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(WRITE)
    @Override
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public CountryEO addDiallingCodeToCountry(Integer countryId, String diallingCode) throws CountryNotFoundException {

        CountryEO managedCountry = super.find(countryId);
        if (managedCountry == null) {
            throw new CountryNotFoundException(
                    new StringBuilder(100)
                            .append("Country with Id: ")
                            .append(countryId)
                            .append(" not found")
                            .toString());
        }

        try {
            managedCountry.addDiallingCode(diallingCode);

            super.edit(managedCountry);
        } catch (OptimisticLockException ex) {
            throw ex;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while adding dialling code to Country with id: {0}",
                    managedCountry.getCountryId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while adding dialling code to Country with id: ")
                            .append(managedCountry.getCountryId())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "Country with id {0} updated successfully.", managedCountry.getCountryId());

        return managedCountry;
    }

    /**
     *
     * @param countryId
     * @param diallingCode
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(WRITE)
    @Override
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public CountryEO removeDiallingCodeFromCountry(Integer countryId, String diallingCode)
            throws CountryNotFoundException {

        CountryEO managedCountry = super.find(countryId);
        if (managedCountry == null) {
            throw new CountryNotFoundException(
                    new StringBuilder(100)
                            .append("Country with Id: ")
                            .append(countryId)
                            .append(" not found")
                            .toString());
        }

        try {
            managedCountry.removeDiallingCode(diallingCode);

            super.edit(managedCountry);
        } catch (OptimisticLockException ex) {
            throw ex;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while removing dialling code from Country with id: {0}",
                    managedCountry.getCountryId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while removing dialling code from Country with id: ")
                            .append(managedCountry.getCountryId())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "Country with id {0} updated successfully.", managedCountry.getCountryId());

        return managedCountry;
    }

    /**
     *
     * @param countryList
     */
    @Override
    // @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = RuntimeException.class)
    public void bulkLoadCountries(List<CountryEO> countryList) {
        Iterator<CountryEO> iter = countryList.iterator();
        while (iter.hasNext()) {
            CountryEO oldCountry = iter.next();
            oldCountry.setCountryId(null);

            // Get the current Region By name
            String regionName = oldCountry.getRegion().getRegion();
            RegionEO managedRegion = regionFacade.getRegionByName(regionName.trim());

            if (managedRegion == null) {
                continue;
            }

            CountryEO country = copyCountry(oldCountry);
            managedRegion.addCountryToList(country);
        }
    }

    private CountryEO copyCountry(CountryEO oldCountry) {

        CountryEO newCountry = new CountryEO();
        newCountry.setCountry(oldCountry.getCountry());
        newCountry.setCountryCode(oldCountry.getCountryCode());

        return newCountry;
    }

    /**
     *
     * @param country
     * @return
     */
    @Override
    public CountryEO getCountryByName(String country) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("country", country);

        List<CountryEO> countryList = super.findWithNamedQuery("CountryEO.findByCountry", parameter);

        if (!countryList.isEmpty()) {
            return countryList.get(0);
        } else {
            return null;
        }
    }

    // @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    @Override
    public void bulkLoadCallingCode(List<CallingCode> callingCodeList) {
        Iterator<CallingCode> iter = callingCodeList.iterator();

        while (iter.hasNext()) {
            CallingCode callCode = iter.next();

            if (callCode != null) {
                CountryEO country = super.find(callCode.getCountryId());

                if (country != null) {
                    country.addCallingCode(callCode.getCallingCode());
                }
            }
        }
    }

    //   @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    @Override
    public void bulkLoadDiallingCode(List<DiallingCode> diallingCodeList) {
        Iterator<DiallingCode> iter = diallingCodeList.iterator();

        while (iter.hasNext()) {
            DiallingCode dialCode = iter.next();

            if (dialCode != null) {
                CountryEO country = super.find(dialCode.getCountryId());

                if (country != null) {
                    country.addCallingCode(dialCode.getDiallingCode());
                }
            }
        }
    }

    //   @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    @Override
    public void bulkLoadPhoneFormat(List<PhoneFormat> phoneFormatList) {
        Iterator<PhoneFormat> iter = phoneFormatList.iterator();

        while (iter.hasNext()) {
            PhoneFormat phoneFormat = iter.next();

            if (phoneFormat != null) {
                CountryEO country = super.find(phoneFormat.getCountryId());

                if (country != null) {
                    country.addPhoneFormat(phoneFormat.getPhoneFormat());
                }
            }
        }
    }
}
