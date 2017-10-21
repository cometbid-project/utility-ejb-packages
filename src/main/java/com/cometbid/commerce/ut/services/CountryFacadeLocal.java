/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.services;

import com.cometbid.commerce.ut.common.DomainObject;
import com.cometbid.commerce.ut.dto.CallingCode;
import com.cometbid.commerce.ut.dto.DiallingCode;
import com.cometbid.commerce.ut.dto.PhoneFormat;
import com.cometbid.ut.entities.CountryEO;
import com.cometbid.ut.entities.CurrencyEO;
import com.cometbid.ut.entities.RegionEO;
import com.cometbid.ut.entities.StateProvEO;
import com.cometbid.ut.exceptions.CountryNotFoundException;
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
public interface CountryFacadeLocal {

    void edit(CountryEO country);

    Map<Integer, Collection<DomainObject>> getCountriesWithCount() throws CountryNotFoundException;

    Collection<DomainObject> getCountriesWithoutCount() throws CountryNotFoundException;

    Map<Integer, Collection<DomainObject>> getCountriesPaginated(Integer pageNumber, Integer pageSize)
            throws CountryNotFoundException;

    CountryEO getCountryById(Integer countryId) throws CountryNotFoundException;

    int getCountryCodeById(Integer countryId) throws CountryNotFoundException;

    String getCountryNameById(Integer countryId) throws CountryNotFoundException;

    String getCountryCapitalCity(Integer countryId) throws CountryNotFoundException;

    long getCountryPopulation(Integer countryId) throws CountryNotFoundException;

    RegionEO getCountryRegion(Integer countryId) throws CountryNotFoundException;

    String getAlphaTwoLetterCode(Integer countryId) throws CountryNotFoundException;

    String getAlphaThreeLetterCode(Integer countryId) throws CountryNotFoundException;

    CurrencyEO getCountryStandardCurrency(Integer countryId) throws CountryNotFoundException;

    Set<String> getCountryCallingCodes(Integer countryId) throws CountryNotFoundException;

    Set<String> getCountryDiallingCodes(Integer countryId) throws CountryNotFoundException;

    Set<Integer> getCountryPhoneFormats(Integer countryId) throws CountryNotFoundException;

    List<StateProvEO> getCountryStateList(Integer countryId) throws CountryNotFoundException;

    CountryEO addCountry(CountryEO newCountry);

    CountryEO editCountry(CountryEO updatedCountry) throws CountryNotFoundException;

    void removeCountry(Integer countryId) throws CountryNotFoundException;

    CountryEO addCallingCodeToCountry(Integer countryId, String callingCode) throws CountryNotFoundException;

    CountryEO removeCallingCodeFromCountry(Integer countryId, String callingCode) throws CountryNotFoundException;

    CountryEO addPhoneFormatToCountry(Integer countryId, Integer phoneFormat) throws CountryNotFoundException;

    CountryEO removePhoneFormatFromCountry(Integer countryId, Integer phoneFormat) throws CountryNotFoundException;

    CountryEO addDiallingCodeToCountry(Integer countryId, String diallingCode) throws CountryNotFoundException;

    CountryEO removeDiallingCodeFromCountry(Integer countryId, String diallingCode) throws CountryNotFoundException;

    Map<Integer, Collection<StateProvEO>> getCountryStateListPaginated(Integer countryId, Integer pageNumber, Integer pageSize)
            throws CountryNotFoundException;

    void bulkLoadCountries(List<CountryEO> countryList);

    CountryEO getCountryByName(String country);

    void bulkLoadCallingCode(List<CallingCode> callingCodeList);

    void bulkLoadDiallingCode(List<DiallingCode> diallingCodeList);

    void bulkLoadPhoneFormat(List<PhoneFormat> phoneFormatList);

}
