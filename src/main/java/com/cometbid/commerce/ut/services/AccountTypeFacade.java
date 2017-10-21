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
import com.cometbid.ut.entities.AccountTypeEO;
import com.cometbid.ut.entities.CountryEO;
import com.cometbid.ut.exceptions.SubscriptionTypeNotFoundException;
import com.jcabi.aspects.RetryOnFailure;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.ejb.AccessTimeout;
import javax.ejb.EJBException;
import javax.ejb.Lock;
import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;
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
//@Interceptors({HitCounterInterceptor.class, TimeInMethodInterceptor.class})
public class AccountTypeFacade extends BatchUploadFacade<AccountTypeEO> implements AccountTypeFacadeLocal {

    @PersistenceContext(unitName = "COMETBID_UT_PU")
    private EntityManager em;

    @Inject
    @JavaUtilLogger
    private Logger logger;

    @Inject
    private MemoryCache memCache;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AccountTypeFacade() {
        super(AccountTypeEO.class);
    }

    /**
     * Uses a Cache as repository. If the Collection returned from Cache is
     * empty, throws SubscriptionTypeNotFoundException, else make the Collection
     * size the key, and actual Collection the value of a <a>java.util.Map</a>
     * and return it.
     *
     * @return <a>java.util.Map</a> the <i>key</i> being the number of record
     * and <i>value</i> being a Collection of Subscriptions
     *
     * @throws com.cometbid.ut.exceptions.SubscriptionTypeNotFoundException
     */
    @Lock(READ)
    @Override
    public Map<Integer, Collection<DomainObject>> getSubscriptionWithCount() throws SubscriptionTypeNotFoundException {

        try {
            Collection<DomainObject> acctTypes = memCache.getUtilityDataMap(GlobalConstants.acctTypes).values();

            if (acctTypes.isEmpty()) {
                logger.log(Level.SEVERE, "No Subscription record found");

                throw new SubscriptionTypeNotFoundException(new StringBuilder(100)
                        .append("No Subscription record found")
                        .toString());
            }

            Map<Integer, Collection<DomainObject>> mapCountRecord = new HashMap<>();
            mapCountRecord.put(acctTypes.size(), acctTypes);

            return Collections.unmodifiableMap(mapCountRecord);

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Subscriptions: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving service", ex);
        }
    }

    /**
     *
     * @return @throws
     * com.cometbid.ut.exceptions.SubscriptionTypeNotFoundException
     */
    @Lock(READ)
    @Override
    public Collection<DomainObject> getSubscriptionWithoutCount() throws SubscriptionTypeNotFoundException {

        try {
            Collection<DomainObject> acctTypes = memCache.getUtilityDataMap(GlobalConstants.acctTypes).values();

            if (acctTypes.isEmpty()) {
                logger.log(Level.SEVERE, "No Subscription record found");

                throw new SubscriptionTypeNotFoundException(new StringBuilder(100)
                        .append("No Subscription record found")
                        .toString());
            }

            return Collections.unmodifiableCollection(acctTypes);
        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Subscriptions: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving service", ex);
        }
    }

    /**
     *
     * @param subscriptionId
     * @return
     * @throws com.cometbid.ut.exceptions.SubscriptionTypeNotFoundException
     */
    @Lock(READ)
    @Override
    public AccountTypeEO getSubscriptionObject(Integer subscriptionId)
            throws SubscriptionTypeNotFoundException {

        try {
            DomainObject subscriptionType = memCache.getUtilityDataMap(GlobalConstants.acctTypes).get(subscriptionId);

            if (subscriptionType == null) {
                throw new SubscriptionTypeNotFoundException(new StringBuilder(100)
                        .append("Subscription type with Id: ")
                        .append(subscriptionId)
                        .append(" not found")
                        .toString());
            }

            return (AccountTypeEO) subscriptionType;

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Subscriptions: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Subscriptions", ex);
        }

    }

    /**
     *
     * @param subscriptionId
     * @return
     * @throws com.cometbid.ut.exceptions.SubscriptionTypeNotFoundException
     */
    @Lock(READ)
    @Override
    public String getSubscriptionType(Integer subscriptionId) throws SubscriptionTypeNotFoundException {

        return getSubscriptionObject(subscriptionId).getAccountType();

    }

    /**
     *
     * @param subscriptionId
     * @return
     * @throws com.cometbid.ut.exceptions.SubscriptionTypeNotFoundException
     */
    @Lock(READ)
    @Override
    public double getPercentageTraxFee(Integer subscriptionId) throws SubscriptionTypeNotFoundException {

        return getSubscriptionObject(subscriptionId).getPercTranFee();
    }

    /**
     *
     * @param subscriptionId
     * @return
     * @throws com.cometbid.ut.exceptions.SubscriptionTypeNotFoundException
     */
    @Lock(READ)
    @Override
    public String getSubscriptionDescription(Integer subscriptionId) throws SubscriptionTypeNotFoundException {

        return getSubscriptionObject(subscriptionId).getDescription();
    }

    /**
     *
     * @param subscriptionId
     * @return
     * @throws com.cometbid.ut.exceptions.SubscriptionTypeNotFoundException
     */
    @Lock(READ)
    @Override
    public double getSpaceQuota(Integer subscriptionId) throws SubscriptionTypeNotFoundException {

        return getSubscriptionObject(subscriptionId).getSpaceQuota();
    }

    /**
     *
     * @param updatedAccountType
     * @return
     * @throws com.cometbid.ut.exceptions.SubscriptionTypeNotFoundException
     */
    @Lock(WRITE)
    @Override
    @Interceptors(ValidationInterceptor.class)
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public AccountTypeEO editAccountType(AccountTypeEO updatedAccountType)
            throws SubscriptionTypeNotFoundException {

        AccountTypeEO managedAccountType = super.find(updatedAccountType.getAcctTypeId());
        if (managedAccountType == null) {
            throw new SubscriptionTypeNotFoundException(
                    new StringBuilder(100)
                            .append("Subscription type with Id: ")
                            .append(updatedAccountType.getAcctTypeId())
                            .append(" not found")
                            .toString()
            );
        }

        try {
            doAccountTypeDataTransfer(managedAccountType, updatedAccountType);
            super.edit(managedAccountType);

        } catch (OptimisticLockException ex) {
            throw ex;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while editing Subscription type with id: {0}",
                    managedAccountType.getAcctTypeId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while editing Subscription type with id: ")
                            .append(managedAccountType.getAcctTypeId())
                            .toString(), exp);
        }

        logger.log(Level.INFO, "Subscription type with id {0} updated successfully.",
                managedAccountType.getAcctTypeId());

        return managedAccountType;
    }

    private void doAccountTypeDataTransfer(AccountTypeEO updatableAccountType, AccountTypeEO updatedAccountType) {
        /*
        updatableAccountType.(updatedAccountType.getAlpha2Code());
        updatableAccountType.setAlpha3Code(updatedAccountType.getAlpha3Code());
        updatableAccountType.setCapitalCity(updatedAccountType.getCapitalCity());
        updatableAccountType.setCountry(updatedAccountType.getCountry());
         */
    }

    /**
     *
     * @param accountList
     */
    @Override
   // @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = RuntimeException.class)
    public void bulkLoadAccountTypes(List<AccountTypeEO> accountList) {
        Iterator<AccountTypeEO> iter = accountList.iterator();
        while (iter.hasNext()) {
            AccountTypeEO accountType = iter.next();

            create(accountType);
        }
    }

}
