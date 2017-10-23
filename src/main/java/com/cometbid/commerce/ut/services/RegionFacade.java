/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.services;

import com.cometbid.commerce.ut.common.BatchUploadFacade;
import com.cometbid.commerce.ut.common.DomainObject;
import com.cometbid.commerce.ut.extra.GlobalConstants;
import com.cometbid.commerce.ut.extra.MemoryCache;
import com.cometbid.commerce.ut.qualifiers.JavaUtilLogger;
import com.cometbid.commerce.ut.qualifiers.Logged;
import com.cometbid.ut.entities.CountryEO;
import com.cometbid.ut.entities.RegionEO;
import com.cometbid.ut.exceptions.RegionNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

/**
 *
 * @author Gbenga
 */
@Stateless
@Logged
public class RegionFacade extends BatchUploadFacade<RegionEO> implements RegionFacadeLocal {

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

    public RegionFacade() {
        super(RegionEO.class);
    }

    /**
     *
     * @return @throws com.cometbid.ut.exceptions.RegionNotFoundException
     */
   // @Lock(READ)
    @Override
    public Map<Integer, Collection<DomainObject>> getRegionsWithCount() throws RegionNotFoundException {

        try {
            Collection<DomainObject> regions = memCache.getUtilityDataMap(GlobalConstants.regions).values();

            if (regions.isEmpty()) {
                logger.log(Level.SEVERE, "No Region record found");

                throw new RegionNotFoundException(
                        new StringBuilder(100)
                                .append("No Region record found")
                                .toString());
            }

            Map<Integer, Collection<DomainObject>> mapCountRecord = new HashMap<>();
            mapCountRecord.put(regions.size(), regions);

            return Collections.unmodifiableMap(mapCountRecord);

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Regions: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Regions", ex);
        }
    }

    /**
     *
     * @return @throws com.cometbid.ut.exceptions.RegionNotFoundException
     */
  //  @Lock(READ)
    @Override
    public Collection<DomainObject> getRegionsWithoutCount() throws RegionNotFoundException {

        try {
            Collection<DomainObject> regionsList = memCache.getUtilityDataMap(GlobalConstants.regions).values();

            if (regionsList.isEmpty()) {
                logger.log(Level.SEVERE, "No Region record found");

                throw new RegionNotFoundException(
                        new StringBuilder(100)
                                .append("No Region record found")
                                .toString());
            }

            return Collections.unmodifiableCollection(regionsList);
        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Regions: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Regions", ex);
        }

    }

    /**
     *
     * @param regionId
     * @return
     * @throws com.cometbid.ut.exceptions.RegionNotFoundException
     */
   // @Lock(READ)
    @Override
    public RegionEO getRegionById(Integer regionId) throws RegionNotFoundException {

        try {
            DomainObject region = memCache.getUtilityDataMap(GlobalConstants.regions).get(regionId);

            if (region == null) {
                throw new RegionNotFoundException(
                        new StringBuilder(100)
                                .append("Region not found by id: ")
                                .append(regionId)
                                .toString());
            }

            return (RegionEO) region;

        } catch (ExecutionException | RuntimeException ex) {
            logger.log(Level.SEVERE, "Unexpected error occured while retrieving Regions: {0}", ex.getMessage());

            throw new EJBException("Unexpected error occured while retrieving Regions", ex);
        }

    }

    /**
     *
     * @param regionId
     * @return
     * @throws com.cometbid.ut.exceptions.RegionNotFoundException
     */
  //  @Lock(READ)
    @Override
    public String getRegionCodeById(Integer regionId) throws RegionNotFoundException {

        return getRegionById(regionId).getRegionCode();
    }

    /**
     *
     * @param regionId
     * @return
     * @throws com.cometbid.ut.exceptions.RegionNotFoundException
     */
  //  @Lock(READ)
    @Override
    public String getRegionNameById(Integer regionId) throws RegionNotFoundException {

        return getRegionById(regionId).getRegion();
    }

    /**
     *
     * @param regionId
     * @return
     * @throws com.cometbid.ut.exceptions.RegionNotFoundException
     */
  //  @Lock(READ)
    @Override
    public List<CountryEO> getRegionCountries(Integer regionId) throws RegionNotFoundException {

        return Collections.unmodifiableList(getRegionById(regionId).getCountryList());
    }

    /**
     *
     * @param regionList
     */
    @Override
    // @RetryOnFailure(attempts = 3, delay = 10, unit = TimeUnit.SECONDS, types = RuntimeException.class)
    public void bulkLoadRegions(List<RegionEO> regionList) {

        Iterator<RegionEO> iter = regionList.iterator();
        while (iter.hasNext()) {
            RegionEO region = iter.next();
            region.setRegionId(null);

            create(region);
        }
    }

    @Override
    public RegionEO getRegionByName(String region) {

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("region", region);
        List<RegionEO> regionList = super.findWithNamedQuery("RegionEO.findByContinent", parameter);

        if (!regionList.isEmpty()) {
            return regionList.get(0);
        } else {
            return null;
        }
    }
}
