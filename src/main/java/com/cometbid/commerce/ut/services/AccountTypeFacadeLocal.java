/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.services;

import com.cometbid.commerce.ut.common.DomainObject;
import com.cometbid.ut.entities.AccountTypeEO;
import com.cometbid.ut.exceptions.SubscriptionTypeNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author Gbenga
 */
@Local
public interface AccountTypeFacadeLocal {

    double getSpaceQuota(Integer subscriptionId) throws SubscriptionTypeNotFoundException;

    String getSubscriptionDescription(Integer subscriptionId) throws SubscriptionTypeNotFoundException;

    double getPercentageTraxFee(Integer subscriptionId) throws SubscriptionTypeNotFoundException;

    String getSubscriptionType(Integer subscriptionId) throws SubscriptionTypeNotFoundException;

    DomainObject getSubscriptionObject(Integer subscriptionId) throws SubscriptionTypeNotFoundException;

    Collection<DomainObject> getSubscriptionWithoutCount() throws SubscriptionTypeNotFoundException;

    Map<Integer, Collection<DomainObject>> getSubscriptionWithCount() throws SubscriptionTypeNotFoundException;

    AccountTypeEO editAccountType(AccountTypeEO updatedAccountType) throws SubscriptionTypeNotFoundException;

    void bulkLoadAccountTypes(List<AccountTypeEO> accountList);

}
