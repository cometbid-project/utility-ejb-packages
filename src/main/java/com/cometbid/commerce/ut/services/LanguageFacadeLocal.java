/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.services;

import com.cometbid.commerce.ut.common.DomainObject;
import com.cometbid.ut.entities.LanguageEO;
import com.cometbid.ut.exceptions.LanguageNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author Gbenga
 */
@Local
public interface LanguageFacadeLocal {

    LanguageEO editLanguage(LanguageEO updatedLanguage)
            throws LanguageNotFoundException;

    Map<Integer, Collection<DomainObject>> getLanguagesWithCount() throws LanguageNotFoundException;

    Collection<DomainObject> getLanguagesWithoutCount() throws LanguageNotFoundException;

    LanguageEO getLanguageById(Integer languageId) throws LanguageNotFoundException;

    String getLanguageCodeById(Integer languageId) throws LanguageNotFoundException;

    String getLanguageNameById(Integer languageId) throws LanguageNotFoundException;

    LanguageEO addLanguage(LanguageEO newLanguage);

    Map<Integer, List<DomainObject>> getLanguagesPaginated(Integer pageNumber, Integer pageSize)
            throws LanguageNotFoundException;

    void removeLanguage(Integer languageId) throws LanguageNotFoundException;

    void bulkLoadLanguages(List<LanguageEO> languageList);
}
