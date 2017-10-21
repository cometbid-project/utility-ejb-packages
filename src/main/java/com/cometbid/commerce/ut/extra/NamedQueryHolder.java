/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.extra;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Gbenga
 */
@Entity
/*@NamedNativeQueries({
@NamedNativeQuery(name = "ACCOUNT_TYPE.MongoDB.findAll",
query = "db.ACCOUNT_TYPES.find()")
,
@NamedNativeQuery(name = "COUNTRY.MongoDB.findAll",
query = "db.COUNTRY_TAB.find( { $query: {}, $orderby: { COUNTRY : 1 } } )")
,
@NamedNativeQuery(name = "CURRENCY.MongoDB.findAll",
query = "db.CURRENCY_TAB.find( { $query: {}, $orderby: { CURRENCY : 1 } } )")
,
@NamedNativeQuery(name = "LANGUAGE.MongoDB.findAll",
query = "db.LANGUAGE_TAB.find( { $query: {}, $orderby: { LANG_NAME : 1 } } )")
,
@NamedNativeQuery(name = "REGION.MongoDB.findAll",
query = "db.REGION_TAB.find()")
,
@NamedNativeQuery(name = "STATE_PROV.MongoDB.findAll",
query = "db.STATE_PROV_TAB.find( { $query: {}, $orderby: { STATE_PROVINCE : 1 } } )")
,
@NamedNativeQuery(name = "ACCOUNT_TYPE.MongoDB.Drop",
query = "db.ACCOUNT_TYPES.drop()")
,
@NamedNativeQuery(name = "COUNTRY.MongoDB.Drop",
query = "db.COUNTRY_TAB.drop()")
,
@NamedNativeQuery(name = "CURRENCY.MongoDB.Drop",
query = "db.CURRENCY_TAB.drop()")
,
@NamedNativeQuery(name = "LANGUAGE.MongoDB.Drop",
query = "db.LANGUAGE_TAB.drop()")
,
@NamedNativeQuery(name = "REGION.MongoDB.Drop",
query = "db.REGION_TAB.drop()")
,
@NamedNativeQuery(name = "STATE_PROV.MongoDB.Drop",
query = "db.STATE_PROV_TAB.drop()")

})*/
public class NamedQueryHolder implements Serializable {

    // entity needs to have an id
    @Id
    private Integer id;

    // getter and setter for id
}
