/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.rs.extra;

import com.cometbid.commerce.ut.dto.CallingCode;
import com.cometbid.commerce.ut.dto.DiallingCode;
import com.cometbid.commerce.ut.dto.PhoneFormat;
import com.cometbid.ut.entities.AccountTypeEO;
import com.cometbid.ut.entities.CountryEO;
import com.cometbid.ut.entities.CurrencyEO;
import com.cometbid.ut.entities.LanguageEO;
import com.cometbid.ut.entities.RegionEO;
import com.cometbid.ut.entities.StateProvEO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gbenga
 */
public class JsonToEntityGenerator {

    private static JsonToEntityGenerator INSTANCE;

    private JsonToEntityGenerator() {
    }

    public static JsonToEntityGenerator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JsonToEntityGenerator();
        }
        return INSTANCE;
    }

    /**
     *
     * @param jsonStr
     * @return
     */
    public List<RegionEO> getRegionListFromJson(String jsonStr) {

        Gson gson = new Gson();

        Map<String, List<RegionEO>> map = gson.fromJson(jsonStr, new TypeToken<Map<String, List<RegionEO>>>() {
        }.getType());

        List<RegionEO> mainJson = (List<RegionEO>) map.values().toArray()[0];

        return mainJson;
    }

    /**
     *
     * @param jsonStr
     * @return
     */
    public List<CurrencyEO> getCurrencyListFromJson(String jsonStr) {

        Gson gson = new Gson();
        Map<String, List<CurrencyEO>> map = gson.fromJson(jsonStr, new TypeToken<Map<String, List<CurrencyEO>>>() {
        }.getType());

        List<CurrencyEO> mainJson = (List<CurrencyEO>) map.values().toArray()[0];

        return mainJson;
    }

    /**
     *
     * @param jsonStr
     * @return
     */
    public List<CountryEO> getCountryListFromJson(String jsonStr) {

        Gson gson = new Gson();
        Map<String, List<CountryEO>> map = gson.fromJson(jsonStr, new TypeToken<Map<String, List<CountryEO>>>() {
        }.getType());

        List<CountryEO> mainJson = (List<CountryEO>) map.values().toArray()[0];

        return mainJson;
    }

    /**
     *
     * @param jsonStr
     * @return
     */
    public List<LanguageEO> getLanguageListFromJson(String jsonStr) {

        Gson gson = new Gson();
        Map<String, List<LanguageEO>> map = gson.fromJson(jsonStr, new TypeToken<Map<String, List<LanguageEO>>>() {
        }.getType());

        List<LanguageEO> mainJson = (List<LanguageEO>) map.values().toArray()[0];

        return mainJson;
    }

    /**
     *
     * @param jsonStr
     * @return
     */
    public List<AccountTypeEO> getSubscriptionListFromJson(String jsonStr) {

        Gson gson = new Gson();
        Map<String, List<AccountTypeEO>> map = gson.fromJson(jsonStr, new TypeToken<Map<String, List<AccountTypeEO>>>() {
        }.getType());

        List<AccountTypeEO> mainJson = (List<AccountTypeEO>) map.values().toArray()[0];

        return mainJson;
    }

    /**
     *
     * @param jsonStr
     * @return
     */
    public List<StateProvEO> getStateListFromJson(String jsonStr) {

        Gson gson = new Gson();
        Map<String, List<StateProvEO>> map = gson.fromJson(jsonStr, new TypeToken<Map<String, List<StateProvEO>>>() {
        }.getType());

        List<StateProvEO> mainJson = (List<StateProvEO>) map.values().toArray()[0];

        return mainJson;
    }

    /**
     *
     * @param jsonStr
     * @return
     */
    public List<CallingCode> getCallingCodesFromJson(String jsonStr) {

        Gson gson = new Gson();
        Map<String, List<CallingCode>> map = gson.fromJson(jsonStr, new TypeToken<Map<String, List<CallingCode>>>() {
        }.getType());

        List<CallingCode> mainJson = (List<CallingCode>) map.values().toArray()[0];

        return mainJson;
    }

    /**
     *
     * @param jsonStr
     * @return
     */
    public List<DiallingCode> getDiallingCodesFromJson(String jsonStr) {

        Gson gson = new Gson();
        Map<String, List<DiallingCode>> map = gson.fromJson(jsonStr, new TypeToken<Map<String, List<DiallingCode>>>() {
        }.getType());

        List<DiallingCode> mainJson = (List<DiallingCode>) map.values().toArray()[0];

        return mainJson;
    }

    /**
     *
     * @param jsonStr
     * @return
     */
    public List<PhoneFormat> getPhoneFormatsFromJson(String jsonStr) {

        Gson gson = new Gson();
        Map<String, List<PhoneFormat>> map = gson.fromJson(jsonStr, new TypeToken<Map<String, List<PhoneFormat>>>() {
        }.getType());

        List<PhoneFormat> mainJson = (List<PhoneFormat>) map.values().toArray()[0];

        return mainJson;
    }

}
