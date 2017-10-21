/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.services;

import com.cometbid.commerce.ut.common.DomainObject;
import com.cometbid.ut.entities.CountryEO;
import com.cometbid.ut.entities.RegionEO;
import com.cometbid.ut.exceptions.RegionNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author Gbenga
 */
@Local
public interface RegionFacadeLocal {
    
    void edit(RegionEO region);

    Collection<CountryEO> getRegionCountries(Integer regionId) throws RegionNotFoundException;

    String getRegionNameById(Integer regionId) throws RegionNotFoundException;

    String getRegionCodeById(Integer regionId) throws RegionNotFoundException;

    RegionEO getRegionById(Integer regionId) throws RegionNotFoundException;

    Collection<DomainObject> getRegionsWithoutCount() throws RegionNotFoundException;

    Map<Integer, Collection<DomainObject>> getRegionsWithCount() throws RegionNotFoundException;

    void bulkLoadRegions(List<RegionEO> regionList);

    RegionEO getRegionByName(String region);
    
}
