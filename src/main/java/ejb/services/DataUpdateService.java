package ejb.services;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *import ejb.update.IndexesToPersist;
 */
import ejb.services.update.entrydata.IndexesToPersist;
import ejb.services.update.entrydatafillers.MoneyFiller;
import ejb.services.update.entrydatafillers.RandomFiller;
import ejb.services.update.entrydatafillers.interfaces.StockDataFiller;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import jpa.*;

/**
 *
 * @author dawid
 */
@Singleton
@Startup
public class DataUpdateService {

    private final Logger log = Logger.getLogger(getClass().getName());

    @PersistenceUnit(unitName = "StockAnalyzerPU")
    private EntityManagerFactory emf;
    private EntityManager em;

    @Resource
    private TimerService timerService;

    @Inject
    @RandomFiller
    private StockDataFiller dataFiller;

    @Inject
    private IndexesToPersist indexData;

    private class StockDataCreatorException extends Exception {

        private static final String sourceMessage = "Exception during stock exchange data initialization: ";

        public StockDataCreatorException() {
            super(sourceMessage);
        }

        public StockDataCreatorException(String message) {
            super(sourceMessage + message);
        }

    }

    @PostConstruct
    private void startup() {
        ScheduleExpression everyDay = new ScheduleExpression()
                .hour("20");
        timerService.createCalendarTimer(everyDay, new TimerConfig("database update started ", false));
        try {
            setupEntityManager();
            Context ctx = new InitialContext();
            boolean isUpdateActive = (boolean) ctx.lookup("java:comp/env/update");
            if (isUpdateActive) {
                this.updateDatabaseStocksExchangeData();
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error. Updating database at startup with stock indexes data has failed: ", ex);
        }
    }

    @Timeout
    public void handleTimer(final javax.ejb.Timer timer) {
        log.info(timer.getInfo() + " at: " + new Date());
        try {
            this.updateDatabaseStocksExchangeData();
        } catch (StockDataCreatorException ex) {
            log.log(Level.SEVERE, "Scheduled database update has failed", ex);
        }
    }

    public StockIndex checkIndexForExistingData(StockIndex index) {
        StockIndex databaseIndex = this.getStockIndexByShortName(index.getShortName());
        if (databaseIndex != null) {
            updateShareDataInExistindIndex(databaseIndex, index);
            return databaseIndex;
        }
        updateShareDataForNewIndex(index);
        return index;
    }

    private void updateShareDataInExistindIndex(StockIndex databaseIndex, StockIndex newIndex) {
        for (Share s : newIndex.getShares()) {
            if (!databaseIndex.containsShare(s)) {
                Share databaseShare = this.getShareByShortName(s.getShortName());
                if (databaseShare != null) {
                    databaseIndex.addShareData(databaseShare);
                } else {
                    databaseIndex.addShareData(s);
                }
            }
        }
    }

    private void updateShareDataForNewIndex(StockIndex index) {
        for (Share s : index.getShares()) {
            Share dbShare = this.getShareByShortName(s.getName());
            if (dbShare != null) {
                index.replaceShareWithEqualShortName(dbShare);
            }
        }
    }

    private void setupEntityManager() {
        em = emf.createEntityManager();
    }

    public void createStockIndex(StockIndex index) {
        em.persist(index);
    }

    public void updateStockIndex(StockIndex index) {
        em.merge(index);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public StockIndex getStockIndexByShortName(String shortName) {
        StockIndex index = null;
        try {
            index = em.createNamedQuery("StockIndex.findByShortName", StockIndex.class).setParameter("shortName", shortName)
                    .getSingleResult();
        } catch (NoResultException ex) {
           log.log(Level.INFO, "{0} for shortname :{1}", new Object[]{ex.getMessage(), shortName});
        }
        return index;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Share getShareByShortName(String shortName) {
        Share share = null;
        try {
            share = em.createNamedQuery("Share.findByShortName", Share.class).setParameter("shortName", shortName)
                    .getSingleResult();
        } catch (NoResultException ex) {
            log.log(Level.INFO, "{0} for shortname :{1}", new Object[]{ex.getMessage(), shortName});
        }
        return share;
    }

    public void updateDatabaseStocksExchangeData() throws StockDataCreatorException {
        List<StockIndex> indexes = indexData.getStockIndexes();
        for (StockIndex index : indexes) {
            index = checkIndexForExistingData(index);
            try {
                dataFiller.fillDataInIndex(index);
                if (index.getId() == null) {
                    log.log(Level.INFO, "creating new Index " + index.getName() + " (" + index.getShortName() + ")");
                    this.createStockIndex(index);
                } else {
                    log.log(Level.INFO, "Updating Index " + index.getName() + " (" + index.getShortName() + ") with ID=" + index.getId());
                    this.updateStockIndex(index);
                }
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Connection Error : Stock Data Update Failed");
            }

        }
    }
}
