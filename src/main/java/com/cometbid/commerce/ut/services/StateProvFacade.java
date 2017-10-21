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
import com.cometbid.commerce.ut.extra.MemoryCache;
import com.cometbid.commerce.ut.qualifiers.JavaUtilLogger;
import com.cometbid.commerce.ut.qualifiers.Logged;
import com.cometbid.ut.embeddables.StateCitiesEO;
import com.cometbid.ut.embeddables.StateLgaEO;
import com.cometbid.ut.entities.CountryEO;
import com.cometbid.ut.entities.StateProvEO;
import com.cometbid.ut.exceptions.CountryNotFoundException;
import com.cometbid.ut.exceptions.StateProvNotFoundException;
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
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.DependsOn;
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
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;

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
public class StateProvFacade extends BatchUploadFacade<StateProvEO> implements StateProvFacadeLocal {

    @PersistenceContext(unitName = "COMETBID_UT_PU")
    private EntityManager em;

    @Inject
    private MemoryCache memCache;
    @Inject
    private CountryFacadeLocal countryFacade;

    private int stateListSize = 0;

    @Inject
    @JavaUtilLogger
    private Logger logger;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public StateProvFacade() {
        super(StateProvEO.class);
    }

    @PostConstruct
    public void init() {

        getRecordCount();
    }

    private void getRecordCount() {

        try {
            List<StateProvEO> statesList = em.createNamedQuery("StateProvEO.findAll")
                    .getResultList();

            if (statesList.isEmpty()) {
                logger.log(Level.INFO, "No record found for States/Provinces types...");
            }

            stateListSize = statesList.size();

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while getting count of State/Province record", ex);

            stateListSize = 0;
        }
    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public Map<Integer, Collection<StateProvEO>> getStateProvWithCount(Integer countryId)
            throws StateProvNotFoundException, CountryNotFoundException {

        try {
            countryFacade.getCountryById(countryId);

            Collection<StateProvEO> stateProv = memCache.getStateProvDataMap(countryId).values();

            if (stateProv.isEmpty()) {
                logger.log(Level.SEVERE, "No State/Province record found");

                throw new StateProvNotFoundException(
                        new StringBuilder(100)
                                .append("No State/Province record found")
                                .toString());
            }

            Map<Integer, Collection<StateProvEO>> mapCountRecord = new HashMap<>();
            mapCountRecord.put(stateProv.size(), stateProv);

            return Collections.unmodifiableMap(mapCountRecord);

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving State/Province by Country id: {0}",
                    countryId);

            throw new EJBException("Unexpected error occured while retrieving State/Province", ex);
        }
    }

    /**
     *
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(READ)
    @Override
    public Collection<StateProvEO> getStateProvWithoutCount(Integer countryId)
            throws StateProvNotFoundException, CountryNotFoundException {

        try {
            countryFacade.getCountryById(countryId);

            Collection<StateProvEO> stateList = memCache.getStateProvDataMap(countryId).values();

            if (stateList.isEmpty()) {
                logger.log(Level.SEVERE, "No State/Province record found");

                throw new StateProvNotFoundException(
                        new StringBuilder(100)
                                .append("No State/Province record found")
                                .toString());
            }

            return Collections.unmodifiableCollection(stateList);
        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE,
                    "Unexpected error occured while retrieving State/Province by Country id: {0}", countryId);

            throw new EJBException("Unexpected error occured while retrieving State/Province", ex);
        }
    }

    /**
     *
     * @param countryId
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(READ)
    @Override
    public Map<Integer, List<StateProvEO>> getStateProvByCountryPaginated(Integer countryId,
            Integer pageNumber, Integer pageSize) throws StateProvNotFoundException {

        try {
            Collection<StateProvEO> stateProvCollection = memCache.getStateProvDataMap(countryId).values();

            if (stateProvCollection.isEmpty()) {
                logger.log(Level.SEVERE, "No State/Province record found by Country Id: {0}", countryId);

                throw new StateProvNotFoundException(
                        new StringBuilder(100)
                                .append("No State/Province record found by Country Id: {0}")
                                .append(countryId)
                                .toString());
            }

            List<StateProvEO> stateProvList = new ArrayList<>(stateProvCollection);
            int fromIndex = (pageNumber - 1) * pageSize;
            int toIndex = (pageNumber * (pageSize - 1)) + pageNumber;

            Map<Integer, List<StateProvEO>> mapCountRecord = new HashMap<>();
            mapCountRecord.put(stateProvList.size(), stateProvList.subList(fromIndex, toIndex));

            return Collections.unmodifiableMap(mapCountRecord);

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving State/Province by Country Id: {0}", countryId);

            throw new EJBException("Unexpected error occured while retrieving countries", ex);
        }
    }

    /**
     *
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(READ)
    @Override
    public Map<Integer, List<StateProvEO>> getStateProvPaginated(Integer pageNumber, Integer pageSize)
            throws StateProvNotFoundException {

        List<StateProvEO> stateProvList = null;

        if (stateListSize <= 0) {
            getRecordCount();
        }

        try {

            int offset = (pageNumber - 1) * pageSize;
            stateProvList = super.findWithNamedQuery("StateProvEO.findAll", offset, pageSize);

            if (stateProvList.isEmpty()) {
                logger.log(Level.SEVERE, "No State/Province record found");

                throw new StateProvNotFoundException(
                        new StringBuilder(100)
                                .append("No State/Province record found")
                                .toString());
            }

            Map<Integer, List<StateProvEO>> mapCountRecord = new HashMap<>();
            mapCountRecord.put(stateListSize, stateProvList);

            logger.log(Level.INFO, "{0} State/Province(s) found!", stateListSize);

            return Collections.unmodifiableMap(mapCountRecord);
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, "Exception occured while retrieving State/Province List");

            throw new EJBException("An unexpected error occured while retrieving State/Province List", ex);
        }
    }

    /**
     *
     * @param stateProvId
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(READ)
    @Override
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public StateProvEO getStateProvById(Integer stateProvId) throws StateProvNotFoundException {

        try {
            StateProvEO stateProv = super.find(stateProvId);

            if (stateProv == null) {
                throw new StateProvNotFoundException(
                        new StringBuilder(100)
                                .append("State/Province not found by id: ")
                                .append(stateProvId)
                                .toString());
            }
            return stateProv;
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, "Exception occured while retrieving State/Province by id: {0}", stateProvId);

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while retrieving State/Province with id: ")
                            .append(stateProvId)
                            .toString(), ex);
        }
    }

    /**
     *
     * @param stateProvId
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(READ)
    @Override
    public String getStateCodeById(Integer stateProvId) throws StateProvNotFoundException {

        return getStateProvById(stateProvId).getStateCode();
    }

    /**
     *
     * @param stateProvId
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(READ)
    @Override
    public String getStateNameById(Integer stateProvId) throws StateProvNotFoundException {

        return getStateProvById(stateProvId).getStateProvince();
    }

    /**
     *
     * @param stateProvId
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(READ)
    @Override
    public int getStateNumCodeById(Integer stateProvId) throws StateProvNotFoundException {

        return getStateProvById(stateProvId).getStateNumCode();
    }

    /**
     *
     * @param stateProvId
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(READ)
    @Override
    public CountryEO getStateCountryById(Integer stateProvId) throws StateProvNotFoundException {

        return getStateProvById(stateProvId).getCountryOb();
    }

    /**
     *
     * @param stateProvId
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(READ)
    @Override
    public Set<StateCitiesEO> getStateCities(Integer stateProvId) throws StateProvNotFoundException {

        return getStateProvById(stateProvId).getCities();
    }

    /**
     *
     * @param stateProvId
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(READ)
    @Override
    public Set<StateLgaEO> getStateLgas(Integer stateProvId) throws StateProvNotFoundException {

        return getStateProvById(stateProvId).getLgas();
    }

    /**
     *
     * @param newStateProv
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     */
    @Lock(WRITE)
    @Override
    @Interceptors(ValidationInterceptor.class)
    public StateProvEO addStateProv(StateProvEO newStateProv, Integer countryId)
            throws CountryNotFoundException {

        CountryEO foundCountry = countryFacade.getCountryById(countryId);
        if (foundCountry == null) {
            throw new CountryNotFoundException(
                    new StringBuilder(100)
                            .append("Country not found by id: ")
                            .append(countryId)
                            .toString());
        }

        /*try {
        super.create(newStateProv);
        em.flush();
        } catch (Exception exp) {
        logger.log(Level.SEVERE, "Exception occured while creating new State/Province: "
        + newStateProv.getStateProvince(), exp);
        throw new EJBException("An unexpected error occured while creating new State/Province: "
        + newStateProv.getStateProvince(), exp);
        }*/
        try {

            foundCountry.addStateProvToList(newStateProv);
            countryFacade.edit(foundCountry);

        } catch (OptimisticLockException ex) {
            throw ex;
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while adding new State/Province to Country with id: {0}",
                    countryId);

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while adding new State/Province to Country with id: ")
                            .append(countryId)
                            .toString(), exp);
        }

        Object[] params = {newStateProv.getStateId(), countryId};
        logger.log(Level.INFO, "State/Province with id {0} added successfully to Country with id: {1}",
                params);

        return newStateProv;
    }

    /**
     *
     * @param updatedStateProv
     * @param countryId
     * @return
     * @throws com.cometbid.ut.exceptions.CountryNotFoundException
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(WRITE)
    @Override
    @Interceptors(ValidationInterceptor.class)
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public StateProvEO editStateProv(StateProvEO updatedStateProv, Integer countryId)
            throws CountryNotFoundException, StateProvNotFoundException {

        CountryEO foundCountry = countryFacade.getCountryById(countryId);
        if (foundCountry == null) {
            throw new CountryNotFoundException(
                    new StringBuilder(100)
                            .append("Country not found by id: ")
                            .append(countryId)
                            .toString());
        }

        StateProvEO managedStateProv = super.find(updatedStateProv.getStateId());
        if (managedStateProv == null) {
            throw new StateProvNotFoundException(
                    new StringBuilder(100)
                            .append("State/Province with Id: ")
                            .append(updatedStateProv.getStateId())
                            .append(" not found")
                            .toString());
        }

        try {
            doStateProvDataTransfer(managedStateProv, updatedStateProv);
            foundCountry.addStateProvToList(managedStateProv);

            countryFacade.edit(foundCountry);

        } catch (OptimisticLockException ex) {
            throw ex;
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while editing State/Province with id: {0}",
                    managedStateProv.getStateId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while editing State/Province with id: ")
                            .append(managedStateProv.getStateId())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "(State/Province) with id {0} updated successfully.",
                managedStateProv.getStateId());
        return managedStateProv;
    }

    private void doStateProvDataTransfer(StateProvEO updatableStateProv, StateProvEO updatedStateProv) {
        updatableStateProv.setStateCode(updatedStateProv.getStateCode());
        updatableStateProv.setStateProvince(updatedStateProv.getStateProvince());
        updatableStateProv.setStateNumCode(updatedStateProv.getStateNumCode());
        updatableStateProv.setDescription(updatedStateProv.getDescription());
    }

    /**
     *
     * @param stateProvId
     * @param countryId
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     * @throws CountryNotFoundException
     */
    @Lock(WRITE)
    @Override
    public void removeStateProv(Integer countryId, Integer stateProvId)
            throws StateProvNotFoundException, CountryNotFoundException {

        CountryEO foundCountry = countryFacade.getCountryById(countryId);
        if (foundCountry == null) {
            throw new CountryNotFoundException(
                    new StringBuilder(100)
                            .append("Country not found by id: ")
                            .append(countryId)
                            .toString());
        }

        try {

            StateProvEO stateProv = getStateProvById(stateProvId);
            foundCountry.removeStateProvFromList(stateProv);
            countryFacade.edit(foundCountry);

        } catch (OptimisticLockException ex) {
            throw ex;
        } catch (RuntimeException exp) {
            logger.log(Level.SEVERE, "Exception occured while removing State/Province with id: {0}",
                    stateProvId);

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while removing State/Province with id: ")
                            .append(stateProvId)
                            .toString(), exp);
        }
        logger.log(Level.INFO, "(State/Province) with id {0} removed successfully.",
                stateProvId);
    }

    /**
     *
     * @param stateId
     * @param cityToAdd
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(WRITE)
    @Override
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public StateProvEO addCityToState(Integer stateId, StateCitiesEO cityToAdd) throws StateProvNotFoundException {

        StateProvEO managedState = super.find(stateId);
        if (managedState == null) {
            throw new StateProvNotFoundException(
                    new StringBuilder(100)
                            .append("State/Province with Id: ")
                            .append(stateId)
                            .append(" not found")
                            .toString());
        }

        try {
            managedState.addStateCity(cityToAdd);
            super.edit(managedState);
        } catch (OptimisticLockException ex) {
            throw ex;
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while adding city to State/Province with id: {0}",
                    managedState.getStateId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while adding city to State/Province with id: ")
                            .append(managedState.getStateId())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "State/Province with id {0} updated successfully.", managedState.getStateId());

        return managedState;
    }

    /**
     *
     * @param stateId
     * @param cityToRemove
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(WRITE)
    @Override
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public StateProvEO removeCityFromState(Integer stateId, StateCitiesEO cityToRemove)
            throws StateProvNotFoundException {

        StateProvEO managedState = super.find(stateId);
        if (managedState == null) {
            throw new StateProvNotFoundException(
                    new StringBuilder(100)
                            .append("State/Province with Id: ")
                            .append(stateId)
                            .append(" not found")
                            .toString());
        }

        try {
            managedState.removeStateCity(cityToRemove);

            super.edit(managedState);
        } catch (OptimisticLockException ex) {
            throw ex;
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while removing city from State/Province with id: {0}",
                    managedState.getStateId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while removing city from State/Province with id: ")
                            .append(managedState.getStateId())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "State/Province with id {0} updated successfully.", managedState.getStateId());

        return managedState;
    }

    /**
     *
     * @param stateId
     * @param lgaToAdd
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(WRITE)
    @Override
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public StateProvEO addLgaToState(Integer stateId, StateLgaEO lgaToAdd) throws StateProvNotFoundException {

        StateProvEO managedState = super.find(stateId);
        if (managedState == null) {
            throw new StateProvNotFoundException(
                    new StringBuilder(100)
                            .append("State/Province with Id: ")
                            .append(stateId)
                            .append(" not found")
                            .toString());
        }

        try {
            managedState.addStateCouncil(lgaToAdd);

            super.edit(managedState);
        } catch (OptimisticLockException ex) {
            throw ex;
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while adding lga to State/Province with id: {0}",
                    managedState.getStateId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while adding lga to State/Province with id: ")
                            .append(managedState.getStateId())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "State/Province with id {0} updated successfully.", managedState.getStateId());

        return managedState;
    }

    /**
     *
     * @param stateId
     * @param lgaToRemove
     * @return
     * @throws com.cometbid.ut.exceptions.StateProvNotFoundException
     */
    @Lock(WRITE)
    @Override
    @Interceptors(ValidationInterceptor.class)
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public StateProvEO removeLgaFromState(Integer stateId, StateLgaEO lgaToRemove)
            throws StateProvNotFoundException {

        StateProvEO managedState = super.find(stateId);
        if (managedState == null) {
            throw new StateProvNotFoundException(
                    new StringBuilder(100)
                            .append("State/Province with Id: ")
                            .append(stateId)
                            .append(" not found")
                            .toString());
        }

        try {
            managedState.removeStateCouncil(lgaToRemove);
            super.edit(managedState);
        } catch (OptimisticLockException ex) {
            throw ex;
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while removing lga from State/Province with id: {0}",
                    managedState.getStateId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while removing lga from State/Province with id: ")
                            .append(managedState.getStateId())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "State/Province with id {0} updated successfully.", managedState.getStateId());

        return managedState;
    }

    /**
     *
     * @param stateList
     */
    @Override
    // @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = RuntimeException.class)
    public void bulkLoadStateProvs(List<StateProvEO> stateList) {
        Iterator<StateProvEO> iter = stateList.iterator();
        while (iter.hasNext()) {
            StateProvEO oldStateProv = iter.next();
            oldStateProv.setStateId(null);

            // Get the current Region By name
            String countryName = oldStateProv.getCountryOb().getCountry();

            CountryEO managedCountry = countryFacade.getCountryByName(countryName.trim());

            if (managedCountry == null) {
                continue;
            }

            StateProvEO newStateProv = copyStateProvince(oldStateProv);

            managedCountry.addStateProvToList(newStateProv);
        }
    }

    private StateProvEO copyStateProvince(StateProvEO oldStateProv) {

        StateProvEO newStateProv = new StateProvEO();
        newStateProv.setStateProvince(oldStateProv.getStateProvince());
        newStateProv.setStateNumCode(oldStateProv.getStateNumCode());
        newStateProv.setStateCode(null);

        return newStateProv;
    }

}
