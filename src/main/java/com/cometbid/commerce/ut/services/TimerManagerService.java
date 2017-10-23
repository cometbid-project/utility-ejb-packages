/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cometbid.commerce.ut.services;

import com.cometbid.commerce.ut.cdi.ApplicationStatistics;
import com.cometbid.commerce.ut.common.BatchUploadFacade;
import com.cometbid.commerce.ut.extra.MemoryCache;
import com.cometbid.commerce.ut.qualifiers.JavaUtilLogger;
import com.cometbid.ut.entities.StatisticsData;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Gbenga
 */
@Stateless
public class TimerManagerService extends BatchUploadFacade<StatisticsData> {

    @PersistenceContext(unitName = "COMETBID_UT_PU")
    private EntityManager entityManager;

    @Inject
    @JavaUtilLogger
    private Logger logger;

    @Inject
    private MemoryCache memCache;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public TimerManagerService() {
        super(StatisticsData.class);
    }

    @Schedule(second = "0", minute = "0/10", hour = "*",
            dayOfWeek = "*", info = "10 minute interval timer",
            persistent = false)
    public void doPeriodicCacheUpdate(Timer t) {

        System.out.println("Saving Statistics gathered...");

        Map<String, StatisticsData> stats = ApplicationStatistics.getInstance().getStatisticsCache();

        super.bulkInsertOrMerge(stats);
        
        stats.clear();
    }

}
