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
import com.cometbid.commerce.ut.extra.GlobalConstants;
import com.cometbid.commerce.ut.extra.MemoryCache;
import com.cometbid.commerce.ut.qualifiers.JavaUtilLogger;
import com.cometbid.commerce.ut.qualifiers.Logged;
import com.cometbid.ut.entities.CurrencyEO;
import com.cometbid.ut.entities.RegionEO;
import com.cometbid.ut.exceptions.CurrencyNotFoundException;
import com.jcabi.aspects.RetryOnFailure;
import java.util.Collection;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.ejb.EJBException;
import javax.ejb.Lock;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.DependsOn;
import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
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
@AccessTimeout(value = 1, unit = TimeUnit.MINUTES)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
// @Interceptors({HitCounterInterceptor.class, TimeInMethodInterceptor.class})
public class CurrencyFacade extends BatchUploadFacade<CurrencyEO> implements CurrencyFacadeLocal {

    @PersistenceContext(unitName = "COMETBID_UT_PU")
    private EntityManager em;

    @Inject
    private MemoryCache memCache;

    @Inject
    @JavaUtilLogger
    private Logger logger;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CurrencyFacade() {
        super(CurrencyEO.class);
    }

    /**
     *
     * @return @throws com.cometbid.ut.exceptions.CurrencyNotFoundException
     */
    @Lock(READ)
    @Override
    public Map<Integer, Collection<DomainObject>> getCurrenciesWithCount() throws CurrencyNotFoundException {

        try {
            Collection<DomainObject> currencies = memCache.getUtilityDataMap(GlobalConstants.currencies).values();

            if (currencies.isEmpty()) {
                logger.log(Level.SEVERE, "No Currency record found");

                throw new CurrencyNotFoundException(
                        new StringBuilder(100)
                                .append("No Currency record found")
                                .toString());
            }

            Map<Integer, Collection<DomainObject>> mapCountRecord = new HashMap<>();
            mapCountRecord.put(currencies.size(), currencies);

            return Collections.unmodifiableMap(mapCountRecord);

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Currencies: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Currencies", ex);
        }
    }

    /**
     *
     * @return @throws com.cometbid.ut.exceptions.CurrencyNotFoundException
     */
    @Lock(READ)
    @Override
    public Collection<DomainObject> getCurrenciesWithoutCount() throws CurrencyNotFoundException {

        try {
            Collection<DomainObject> currencyList = memCache.getUtilityDataMap(GlobalConstants.currencies).values();
            if (currencyList.isEmpty()) {
                logger.log(Level.SEVERE, "No Currency record found");

                throw new CurrencyNotFoundException(
                        new StringBuilder(100)
                                .append("No Currency record found")
                                .toString());
            }

            return Collections.unmodifiableCollection(currencyList);
        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Currencies: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Currencies", ex);
        }
    }

    /**
     *
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws com.cometbid.ut.exceptions.CurrencyNotFoundException
     */
    @Lock(READ)
    @Override
    public Map<Integer, Collection<DomainObject>> getCurrenciesPaginated(Integer pageNumber, Integer pageSize)
            throws CurrencyNotFoundException {

        try {
            Collection<DomainObject> currencyCollection = memCache.getUtilityDataMap(GlobalConstants.currencies).values();

            if (currencyCollection.isEmpty()) {
                logger.log(Level.SEVERE, "No Currency record found");

                throw new CurrencyNotFoundException(
                        new StringBuilder(100)
                                .append("No Currency record found")
                                .toString());
            }
            List<DomainObject> currencyList = new ArrayList<>(currencyCollection);
            int fromIndex = (pageNumber - 1) * pageSize;
            int toIndex = (pageNumber * (pageSize - 1)) + pageNumber;

            Map<Integer, Collection<DomainObject>> mapCountRecord = new HashMap<>();
            mapCountRecord.put(currencyList.size(), currencyList.subList(fromIndex, toIndex));

            return Collections.unmodifiableMap(mapCountRecord);

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Currencies: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Currencies", ex);
        }
    }

    /**
     *
     * @param currencyId
     * @return
     * @throws com.cometbid.ut.exceptions.CurrencyNotFoundException
     */
    @Lock(READ)
    @Override
    public CurrencyEO getCurrencyById(Integer currencyId) throws CurrencyNotFoundException {

        try {
            DomainObject currency = memCache.getUtilityDataMap(GlobalConstants.currencies).get(currencyId);

            if (currency == null) {
                throw new CurrencyNotFoundException(
                        new StringBuilder(100)
                                .append("Currency with Id: ")
                                .append(currencyId)
                                .append(" not found")
                                .toString());
            }

            return (CurrencyEO) currency;
        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Currencies: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Currencies", ex);
        }

    }

    /**
     *
     * @param currencyId
     * @return
     * @throws com.cometbid.ut.exceptions.CurrencyNotFoundException
     */
    @Lock(READ)
    @Override
    public String getCurrencyCodeById(Integer currencyId) throws CurrencyNotFoundException {

        return getCurrencyById(currencyId).getCurrCode();
    }

    /**
     *
     * @param currencyId
     * @return
     * @throws com.cometbid.ut.exceptions.CurrencyNotFoundException
     */
    @Lock(READ)
    @Override
    public String getCurrencyNameById(Integer currencyId) throws CurrencyNotFoundException {

        return getCurrencyById(currencyId).getCurrency();
    }

    /**
     *
     * @param fromCurrencyCode
     * @param toCurrencyCode
     * @return @throws com.cometbid.ut.exceptions.CurrencyNotFoundException
     */
    /*@Lock(READ)
    @Override
    public Money getCurrencyExRate(String fromCurrencyCode, String toCurrencyCode)
    throws CurrencyNotFoundException {
    
    double AMOUNT_TO_CONVERT = 1;
    return doCurrencyConversion(fromCurrencyCode, toCurrencyCode, AMOUNT_TO_CONVERT);
    }*/
    /**
     *
     * @param fromCurrencyCode
     * @param toCurrencyCode
     * @param amountToConvert
     * @return @throws com.cometbid.ut.exceptions.CurrencyNotFoundException
     */
    /*@Lock(READ)
    @Override
    public Money doCurrencyConversion(String fromCurrencyCode, String toCurrencyCode, double amountToConvert)
    throws CurrencyNotFoundException {
    
    MonetaryAmount fromAmount = Monetary.getDefaultAmountFactory().setCurrency(fromCurrencyCode)
    .setNumber(amountToConvert).create();
    
    CurrencyConversion currencyConvertedTo = MonetaryConversions.getConversion(toCurrencyCode);
    
    MonetaryAmount convertedAmount = fromAmount.with(currencyConvertedTo);
    
    return Money.of(convertedAmount.getNumber(),
    convertedAmount.getCurrency(),
    convertedAmount.getContext());
    }*/
    /**
     *
     * @param currencyId
     * @return
     * @throws com.cometbid.ut.exceptions.CurrencyNotFoundException
     */
    @Lock(READ)
    @Override
    public String getCurrencySymbol(Integer currencyId) throws CurrencyNotFoundException {

        return getCurrencyById(currencyId).getHtmlSymbol();
    }

    /**
     *
     * @param newCurrency
     * @return
     */
    @Lock(WRITE)
    @Override
    @Interceptors(ValidationInterceptor.class)
    public CurrencyEO addCurrency(CurrencyEO newCurrency) {

        try {
            super.create(newCurrency);

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while creating new Currency: {0}",
                    newCurrency.getCurrCode());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while creating new Currency: ")
                            .append(newCurrency.getCurrCode())
                            .toString(), exp);
        }

        logger.log(Level.INFO, "{0} created successfully.", newCurrency.getCurrencyId());

        return newCurrency;
    }

    /**
     *
     * @param updatedCurrency
     * @return
     * @throws com.cometbid.ut.exceptions.CurrencyNotFoundException
     */
    @Lock(WRITE)
    @Override
    @Interceptors(ValidationInterceptor.class)
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public CurrencyEO editCurrency(CurrencyEO updatedCurrency)
            throws CurrencyNotFoundException {

        CurrencyEO managedCurrency = super.find(updatedCurrency.getCurrencyId());
        if (managedCurrency == null) {
            throw new CurrencyNotFoundException(
                    new StringBuilder(100)
                            .append("Currency with Id: ")
                            .append(updatedCurrency.getCurrencyId())
                            .append(" not found")
                            .toString());
        }

        try {
            doCurrencyDataTransfer(managedCurrency, updatedCurrency);
            super.edit(managedCurrency);

        } catch (OptimisticLockException ex) {
            throw ex;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while updating currency with code: {0}",
                    updatedCurrency.getCurrCode());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while updating currency with code: ")
                            .append(updatedCurrency.getCurrCode())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "Currency with id {0} updated successfully.", managedCurrency.getCurrencyId());

        return managedCurrency;
    }

    private void doCurrencyDataTransfer(CurrencyEO updatableCurrency, CurrencyEO updatedCurrency) {
        updatableCurrency.setCurrCode(updatedCurrency.getCurrCode());
        updatableCurrency.setCurrency(updatedCurrency.getCurrency());
        updatableCurrency.setExRate(updatedCurrency.getExRate());
        updatableCurrency.setHtmlSymbol(updatedCurrency.getHtmlSymbol());
        updatableCurrency.setDescription(updatedCurrency.getDescription());
        updatableCurrency.setCurrencySupported(updatedCurrency.isCurrencySupported());
    }

    /**
     *
     * @param currencyId
     * @throws com.cometbid.ut.exceptions.CurrencyNotFoundException
     */
    @Lock(WRITE)
    @Override
    public void removeCurrency(Integer currencyId) throws CurrencyNotFoundException {

        CurrencyEO managedCurrency = super.find(currencyId);
        if (managedCurrency == null) {
            throw new CurrencyNotFoundException(
                    new StringBuilder(100)
                            .append("Currency with Id: ")
                            .append(currencyId)
                            .append(" not found")
                            .toString());
        }

        try {
            super.remove(managedCurrency);

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while editing Currency with id: {0}",
                    managedCurrency.getCurrencyId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while editing Currency with id: ")
                            .append(managedCurrency.getCurrencyId())
                            .toString(), exp);
        }

        logger.log(Level.INFO, "Currency with id {0} has been removed successfully.",
                managedCurrency.getCurrencyId());
    }

    @Override
  //  @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = RuntimeException.class)
    public void bulkLoadCurrencies(List<CurrencyEO> currencyList) {
        Iterator<CurrencyEO> iter = currencyList.iterator();
        while (iter.hasNext()) {
            CurrencyEO currency = iter.next();
            currency.setCurrencyId(null);

            create(currency);
        }
    }
}
