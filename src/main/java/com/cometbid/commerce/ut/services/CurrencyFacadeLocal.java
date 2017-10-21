/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.services;

import com.cometbid.commerce.ut.common.DomainObject;
import com.cometbid.ut.entities.CurrencyEO;
import com.cometbid.ut.exceptions.CurrencyNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author Gbenga
 */
@Local
public interface CurrencyFacadeLocal {

    Collection<DomainObject> getCurrenciesWithoutCount() throws CurrencyNotFoundException;

    /*  
    Money getCurrencyExRate(String fromCurrencyCode, String toCurrencyCode)
    throws CurrencyNotFoundException;
     */

 /*
    Money doCurrencyConversion(String fromCurrencyCode, String toCurrencyCode,
    double amountToConvert) throws CurrencyNotFoundException;
     */
    Map<Integer, Collection<DomainObject>> getCurrenciesWithCount() throws CurrencyNotFoundException;

    CurrencyEO editCurrency(CurrencyEO updatedCurrency)
            throws CurrencyNotFoundException;

    CurrencyEO addCurrency(CurrencyEO newCurrency);

    Map<Integer, Collection<DomainObject>> getCurrenciesPaginated(Integer pageNumber, Integer pageSize)
            throws CurrencyNotFoundException;

    CurrencyEO getCurrencyById(Integer currencyId) throws CurrencyNotFoundException;

    void removeCurrency(Integer currencyId) throws CurrencyNotFoundException;

    String getCurrencyCodeById(Integer currencyId) throws CurrencyNotFoundException;

    String getCurrencyNameById(Integer currencyId) throws CurrencyNotFoundException;

    String getCurrencySymbol(Integer currencyId) throws CurrencyNotFoundException;

    void bulkLoadCurrencies(List<CurrencyEO> currencyList);

}
