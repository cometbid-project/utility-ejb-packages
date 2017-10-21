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
import com.cometbid.ut.entities.CountryEO;
import com.cometbid.ut.entities.LanguageEO;
import com.cometbid.ut.exceptions.LanguageNotFoundException;
import com.jcabi.aspects.RetryOnFailure;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class LanguageFacade extends BatchUploadFacade<LanguageEO> implements LanguageFacadeLocal {

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

    public LanguageFacade() {
        super(LanguageEO.class);
    }

    /**
     *
     * @return @throws com.cometbid.ut.exceptions.LanguageNotFoundException
     */
    @Lock(READ)
    @Override
    public Map<Integer, Collection<DomainObject>> getLanguagesWithCount() throws LanguageNotFoundException {

        try {
            Collection<DomainObject> languages = memCache.getUtilityDataMap(GlobalConstants.languages).values();

            if (languages.isEmpty()) {
                logger.log(Level.SEVERE, "No Language record found");

                throw new LanguageNotFoundException(
                        new StringBuilder(100)
                                .append("No Language record found")
                                .toString());
            }
            Map<Integer, Collection<DomainObject>> mapCountRecord = new HashMap<>();

            mapCountRecord.put(languages.size(), languages);
            return Collections.unmodifiableMap(mapCountRecord);

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Languages: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Languages", ex);
        }
    }

    /**
     *
     * @return @throws com.cometbid.ut.exceptions.LanguageNotFoundException
     */
    @Lock(READ)
    @Override
    public Collection<DomainObject> getLanguagesWithoutCount() throws LanguageNotFoundException {

        try {
            Collection<DomainObject> languageList = memCache.getUtilityDataMap(GlobalConstants.languages).values();

            if (languageList.isEmpty()) {
                logger.log(Level.SEVERE, "No Language record found");

                throw new LanguageNotFoundException(
                        new StringBuilder(100)
                                .append("No Language record found")
                                .toString());
            }

            return Collections.unmodifiableCollection(languageList);

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Languages: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Languages", ex);
        }
    }

    /**
     *
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws com.cometbid.ut.exceptions.LanguageNotFoundException
     */
    @Lock(READ)
    @Override
    public Map<Integer, List<DomainObject>> getLanguagesPaginated(Integer pageNumber, Integer pageSize)
            throws LanguageNotFoundException {

        try {
            Collection<DomainObject> languageCollection = memCache.getUtilityDataMap(GlobalConstants.languages).values();

            if (languageCollection.isEmpty()) {
                logger.log(Level.SEVERE, "No Language record found");

                throw new LanguageNotFoundException(
                        new StringBuilder(100)
                                .append("No Language record found")
                                .toString());
            }

            List<DomainObject> currencyList = new ArrayList<>(languageCollection);
            int fromIndex = (pageNumber - 1) * pageSize;
            int toIndex = (pageNumber * (pageSize - 1)) + pageNumber;

            Map<Integer, List<DomainObject>> mapCountRecord = new HashMap<>();
            mapCountRecord.put(currencyList.size(), currencyList.subList(fromIndex, toIndex));

            return Collections.unmodifiableMap(mapCountRecord);

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Languages: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Languages", ex);
        }
    }

    /**
     *
     * @param langaugeId
     * @return
     * @throws com.cometbid.ut.exceptions.LanguageNotFoundException
     */
    @Lock(READ)
    @Override
    public LanguageEO getLanguageById(Integer langaugeId) throws LanguageNotFoundException {

        try {
            DomainObject language = memCache.getUtilityDataMap(GlobalConstants.languages).get(langaugeId);

            if (language == null) {
                throw new LanguageNotFoundException(
                        new StringBuilder(100)
                                .append("Language not found by id: ")
                                .append(langaugeId)
                                .toString());
            }

            return (LanguageEO) language;

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Languages: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Languages", ex);
        }
    }

    /**
     *
     * @param languageId
     * @return
     * @throws com.cometbid.ut.exceptions.LanguageNotFoundException
     */
    @Lock(READ)
    @Override
    public String getLanguageCodeById(Integer languageId) throws LanguageNotFoundException {

        return getLanguageById(languageId).getLangCode();
    }

    /**
     *
     * @param languageId
     * @return
     * @throws com.cometbid.ut.exceptions.LanguageNotFoundException
     */
    @Lock(READ)
    @Override
    public String getLanguageNameById(Integer languageId) throws LanguageNotFoundException {

        return getLanguageById(languageId).getLangName();

    }

    /**
     *
     * @param newLanguage
     * @return
     */
    @Lock(WRITE)
    @Override
    @Interceptors(ValidationInterceptor.class)
    public LanguageEO addLanguage(LanguageEO newLanguage) {

        try {
            super.create(newLanguage);
        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while creating new Language with code: {0}",
                    newLanguage.getLangCode());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while creating new Language with code: ")
                            .append(newLanguage.getLangCode())
                            .toString(), exp);
        }

        logger.log(Level.INFO, "Language with id {0} has been created successfully.", newLanguage.getLangId());

        return newLanguage;
    }

    /**
     *
     * @param updatedLanguage
     * @return
     * @throws com.cometbid.ut.exceptions.LanguageNotFoundException
     */
    @Lock(WRITE)
    @Override
    @Interceptors(ValidationInterceptor.class)
    @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = OptimisticLockException.class)
    public LanguageEO editLanguage(LanguageEO updatedLanguage)
            throws LanguageNotFoundException {

        LanguageEO managedLanguage = super.find(updatedLanguage.getLangId());
        if (managedLanguage == null) {
            throw new LanguageNotFoundException(
                    new StringBuilder(100)
                            .append("Language with Id: ")
                            .append(updatedLanguage.getLangId())
                            .append(" not found")
                            .toString());
        }

        try {

            doLanguageDataTransfer(managedLanguage, updatedLanguage);
            super.edit(managedLanguage);

        } catch (OptimisticLockException ex) {
            throw ex;

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while updating language with id: {0}",
                    managedLanguage.getLangId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while updating language with id: ")
                            .append(managedLanguage.getLangId())
                            .toString(), exp);
        }
        logger.log(Level.INFO, "Language with id {0} has been updated successfully.", managedLanguage.getLangId());

        return managedLanguage;
    }

    private void doLanguageDataTransfer(LanguageEO updatableLanguage, LanguageEO updatedLanguage) {
        updatableLanguage.setLangCode(updatedLanguage.getLangCode());
        updatableLanguage.setLangName(updatedLanguage.getLangName());
        updatableLanguage.setDescription(updatedLanguage.getDescription());
        updatableLanguage.setLangSupported(updatedLanguage.isLangSupported());
    }

    /**
     *
     * @param languageId
     * @throws com.cometbid.ut.exceptions.LanguageNotFoundException
     */
    @Lock(WRITE)
    @Override
    public void removeLanguage(Integer languageId) throws LanguageNotFoundException {

        LanguageEO managedLanguage = super.find(languageId);
        if (managedLanguage == null) {
            throw new LanguageNotFoundException(
                    new StringBuilder(100)
                            .append("Language with Id: ")
                            .append(languageId)
                            .append(" not found")
                            .toString());
        }

        try {
            super.remove(managedLanguage);

        } catch (Exception exp) {
            logger.log(Level.SEVERE, "Exception occured while removing Language with id: {0}",
                    managedLanguage.getLangId());

            throw new EJBException(
                    new StringBuilder(100)
                            .append("An unexpected error occured while removing Language with id: ")
                            .append(managedLanguage.getLangId())
                            .toString(), exp);
        }

        logger.log(Level.INFO, "Language with id {0} has been removed successfully.",
                managedLanguage.getLangId());
    }

    @Override
    //   @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = RuntimeException.class)
    public void bulkLoadLanguages(List<LanguageEO> languageList) {
        Iterator<LanguageEO> iter = languageList.iterator();
        while (iter.hasNext()) {
            LanguageEO language = iter.next();
            language.setLangId(null);

            create(language);
        }
    }

}
