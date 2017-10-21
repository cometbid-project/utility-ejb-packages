/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.services;

import com.cometbid.ut.embeddables.StateCitiesEO;
import com.cometbid.ut.embeddables.StateLgaEO;
import com.cometbid.ut.entities.CountryEO;
import com.cometbid.ut.entities.StateProvEO;
import com.cometbid.ut.exceptions.CountryNotFoundException;
import com.cometbid.ut.exceptions.StateProvNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Local;

/**
 *
 * @author Gbenga
 */
@Local
public interface StateProvFacadeLocal {

    void removeStateProv(Integer countryId, Integer stateProvId)
            throws StateProvNotFoundException, CountryNotFoundException;

    StateProvEO editStateProv(StateProvEO updatedStateProv, Integer countryId)
            throws CountryNotFoundException, StateProvNotFoundException;

    StateProvEO addStateProv(StateProvEO newStateProv, Integer countryId)
            throws CountryNotFoundException;

    Set<StateLgaEO> getStateLgas(Integer stateProvId) throws StateProvNotFoundException;

    Set<StateCitiesEO> getStateCities(Integer stateProvId) throws StateProvNotFoundException;

    CountryEO getStateCountryById(Integer stateProvId) throws StateProvNotFoundException;

    String getStateCodeById(Integer stateProvId) throws StateProvNotFoundException;

    StateProvEO getStateProvById(Integer stateProvId) throws StateProvNotFoundException;

    // List<StateProvEO> getCountriesWithoutCount(String countryId) throws CountryNotFoundException;
    // Map<String, List<StateProvEO>> getStatesWithCount(String countryId) throws CountryNotFoundException;
    String getStateNameById(Integer stateProvId) throws StateProvNotFoundException;

    int getStateNumCodeById(Integer stateProvId) throws StateProvNotFoundException;

    Collection<StateProvEO> getStateProvWithoutCount(Integer countryId)
                    throws StateProvNotFoundException, CountryNotFoundException;

    Map<Integer, Collection<StateProvEO>> getStateProvWithCount(Integer countryId)
            throws StateProvNotFoundException, CountryNotFoundException;

    StateProvEO addCityToState(Integer stateId, StateCitiesEO cityToAdd) throws StateProvNotFoundException;

    StateProvEO removeCityFromState(Integer stateId, StateCitiesEO cityToRemove) throws StateProvNotFoundException;

    StateProvEO addLgaToState(Integer stateId, StateLgaEO lgaToAdd) throws StateProvNotFoundException;

    StateProvEO removeLgaFromState(Integer stateId, StateLgaEO lgaToRemove) throws StateProvNotFoundException;

    Map<Integer, List<StateProvEO>> getStateProvByCountryPaginated(Integer countryId,
            Integer pageNumber, Integer pageSize)
            throws StateProvNotFoundException;

    Map<Integer, List<StateProvEO>> getStateProvPaginated(Integer pageNumber, Integer pageSize) 
            throws StateProvNotFoundException;

    void bulkLoadStateProvs(List<StateProvEO> stateList);

}
