/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.services;

import dto.*;

import ejb.services.enums.PricesGroupingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.*;
import jpa.*;

/**
 *
 * @author dawid
 */
@Stateless
public class ShareService  {

    @PersistenceContext(unitName = "StockAnalyzerPU")
    private EntityManager em;

    public void createStockIndex(StockIndex index) {
        em.persist(index);
    }

    public void createDailyPrices(DailyPrices s) {
        em.persist(s);
    }

    public void createShare(Share index) {
        em.persist(index);
    }

    public void addShareDataToIndex(Share share, long indexId) {
        StockIndex index = em.find(StockIndex.class, indexId);
        index.addShareData(share);
        em.merge(index);
    }

    public StockIndex getStockIndexById(long id) {
        return em.find(StockIndex.class, id);
    }

    public Share getShareByID(long id) {
        return em.find(Share.class, id);
    }

    public StockIndexDTO getStockIndexDTOByID(long id) {
        StockIndex index = this.getStockIndexById(id);
        return index == null ? null : index.transformToDTO();
    }

    public StockIndexDTO getStockIndexDTOByShortName(String shortName) {
        StockIndex index = this.getStockIndexByShortName(shortName);
        return index == null ? null : index.transformToDTO();
    }

    public ShareDTO getShareDTOByID(long id) {
        Share s = this.getShareByID(id);
        return s == null ? null : s.transformToDTO();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ShareDTO getShareDTOByShortName(String shortName) {
        Share s = this.getShareByShortName(shortName);
        return s == null ? null : s.transformToDTO();
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public StockIndex getStockIndexByShortName(String shortName) {
        StockIndex index = null;
        try {
            index = em.createNamedQuery("StockIndex.findByShortName", StockIndex.class).setParameter("shortName", shortName)
                    .getSingleResult();
        } catch (NoResultException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "{0} for shortname :{1}", new Object[]{ex.getMessage(), shortName});
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
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, ex.getMessage());
        }
        return share;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<StockIndex> getAllStockIndexes() {
        return em.createNamedQuery("StockIndex.findAll", StockIndex.class).getResultList();
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Share> getAllSharesInIndex(long indexId) {
        List<Share> shares = getStockIndexById(indexId).getShares();
        return shares;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<DailyPrices> getPricesListById(long shareId) {
        List<DailyPrices> prices = em.find(PricesHolder.class, shareId).getDailyPriceData();
        return prices;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<DailyPrices> getPagedDailyPrices(long holderID, int page, int pageSize) {
        List<DailyPrices> resultList = em.createNamedQuery("PricesHolder.findAll", DailyPrices.class)
                .setParameter("id", holderID)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
        return resultList;

    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<StockIndexDTO> getAllDTOStockIndexes() {
        List<StockIndexDTO> indexes = new ArrayList<StockIndexDTO>();
        for (StockIndex i : getAllStockIndexes()) {
            indexes.add(i.transformToDTO());
        }
        return indexes;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ShareDTO> getAllSharesDTOInIndex(long indexId) {
        List<ShareDTO> shares = new ArrayList<ShareDTO>();
        for (Share s : getAllSharesInIndex(indexId)) {
            shares.add(s.transformToDTO());
        }
        return shares;
    }
    
    private void setOpenAndCloseValuesForPricesDateWeekly(List<PeriodPricesDTO> periods, long holderID) {
        if(periods.isEmpty())
            return;
        Date first = periods.get(0).getBeginDate();
        Date last = periods.get(periods.size()-1).getEndDate();
        List<DailyPrices> prices = em.createNamedQuery("PricesHolder.findAllRange", DailyPrices.class)
                .setParameter("id", holderID).setParameter("dateFrom", first).setParameter("dateTo", last)
                .getResultList();
        int j = 0;
       for(int i=0; i<periods.size(); i++){
           DailyPrices dailyPrice =prices.get(j);
           PeriodPricesDTO periodPrice = periods.get(i);
           float open = dailyPrice.getOpen();
           int week = dailyPrice.getWeekNumber();
           boolean notEnd = true;
           while((notEnd=(j+1<prices.size())) && (dailyPrice=prices.get(++j)).getWeekNumber()==week);
           float close=0;
           if(notEnd){
                close = prices.get(j-1).getClose();               
           }
           else{
               close = dailyPrice.getClose();
           }
           float change = ((close - open)/open) * 100;
           periodPrice.setOpen(open);
           periodPrice.setClose(close);
           periodPrice.setChange(change);
       }
    }
    
    private void setOpenAndCloseValuesForPricesDateMonthly(List<PeriodPricesDTO> periods, long holderID) {
        if(periods.isEmpty())
            return;
        Date first = periods.get(0).getBeginDate();
        Date last = periods.get(periods.size()-1).getEndDate();
        List<DailyPrices> prices = em.createNamedQuery("PricesHolder.findAllRange", DailyPrices.class)
                .setParameter("id", holderID).setParameter("dateFrom", first).setParameter("dateTo", last)
                .getResultList();
        int j = 0;
       for(int i=0; i<periods.size(); i++){
           DailyPrices dailyPrice =prices.get(j);
           PeriodPricesDTO periodPrice = periods.get(i);
           float open = dailyPrice.getOpen();
           int month = dailyPrice.getMonthNumber();
           boolean notEnd = true;
           while((notEnd=(j+1<prices.size())) && (dailyPrice=prices.get(++j)).getMonthNumber()==month);
           float close=0;
           if(notEnd){
                close = prices.get(j-1).getClose();               
           }
           else{
               close = dailyPrice.getClose();
           }
           float change = ((close - open)/open) * 100;
           periodPrice.setOpen(open);
           periodPrice.setClose(close);
           periodPrice.setChange(change);
       }
    }


    public void updateStockIndex(StockIndex index) {
        em.merge(index);
    }

    public void updateShareData(Share s) {
        em.merge(s);
    }

    public void updateDailyPrices(DailyPrices d) {
        em.merge(d);
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PeriodPricesDTO> getDailyPeriodPrices(long holderID) {
        List<PeriodPricesDTO> periodsList = em.createNamedQuery("DailyPrices.getDailyPrices", PeriodPricesDTO.class)
                .setParameter("id", holderID)
                .getResultList();
        return periodsList;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PeriodPricesDTO> getDailyPeriodPrices(long holderID, int page, int pageSize) {
        List<PeriodPricesDTO> periodsList = em.createNamedQuery("DailyPrices.getDailyPrices", PeriodPricesDTO.class)
                .setParameter("id", holderID).setFirstResult(page * pageSize).setMaxResults(pageSize)
                .getResultList();
        return periodsList;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PeriodPricesDTO> getDailyPeriodPrices(long holderID, Date from, Date to) {
        List<PeriodPricesDTO> periodsList = em.createNamedQuery("DailyPrices.getDailyPricesRange", PeriodPricesDTO.class)
                .setParameter("id", holderID).setParameter("dateFrom", from).setParameter("dateTo", to)
                .getResultList();
        return periodsList;
    }

    public List<PeriodPricesDTO> getDailyPeriodPrices(long holderID, int page, int pageSize, Date from, Date to) {
        List<PeriodPricesDTO> periodsList = em.createNamedQuery("DailyPrices.getDailyPricesRange", PeriodPricesDTO.class)
                .setParameter("id", holderID).setParameter("dateFrom", from).setParameter("dateTo", to)
                .setFirstResult(page * pageSize).setMaxResults(pageSize)
                .getResultList();

        return periodsList;
    }

            @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PeriodPricesDTO> getWeeklyPeriodPrices(long holderID) {
        List<PeriodPricesDTO> periodsList = em.createNamedQuery("DailyPrices.getWeeklyPrices", PeriodPricesDTO.class)
                .setParameter("id", holderID)
                .getResultList();
        this.setOpenAndCloseValuesForPricesDateWeekly(periodsList, holderID);
        return periodsList;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PeriodPricesDTO> getWeeklyPeriodPrices(long holderID, int page, int pageSize) {
        List<PeriodPricesDTO> periodsList = em.createNamedQuery("DailyPrices.getWeeklyPrices", PeriodPricesDTO.class)
                .setParameter("id", holderID).setFirstResult(page * pageSize).setMaxResults(pageSize)
                .getResultList();
        this.setOpenAndCloseValuesForPricesDateWeekly(periodsList, holderID);
        return periodsList;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PeriodPricesDTO> getWeeklyPeriodPrices(long holderID, Date from, Date to) {
        List<PeriodPricesDTO> periodsList = em.createNamedQuery("DailyPrices.getWeeklyPricesRange", PeriodPricesDTO.class)
                .setParameter("id", holderID).setParameter("dateFrom", from).setParameter("dateTo", to)
                .getResultList();
        this.setOpenAndCloseValuesForPricesDateWeekly(periodsList, holderID);
        return periodsList;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PeriodPricesDTO> getWeeklyPeriodPrices(long holderID, int page, int pageSize, Date from, Date to) {
        List<PeriodPricesDTO> periodsList = em.createNamedQuery("DailyPrices.getWeeklyPricesRange", PeriodPricesDTO.class)
                .setParameter("id", holderID).setParameter("dateFrom", from).setParameter("dateTo", to)
                .setFirstResult(page * pageSize).setMaxResults(pageSize)
                .getResultList();
        this.setOpenAndCloseValuesForPricesDateWeekly(periodsList, holderID);
        return periodsList;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PeriodPricesDTO> getMonthlyPeriodPrices(long holderID) {
        List<PeriodPricesDTO> periodsList = em.createNamedQuery("DailyPrices.getMonthlyPrices", PeriodPricesDTO.class)
                .setParameter("id", holderID)
                .getResultList();
        this.setOpenAndCloseValuesForPricesDateMonthly(periodsList, holderID);
        return periodsList;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PeriodPricesDTO> getMonthlyPeriodPrices(long holderID, int page, int pageSize) {
        List<PeriodPricesDTO> periodsList = em.createNamedQuery("DailyPrices.getMonthlyPrices", PeriodPricesDTO.class)
                .setParameter("id", holderID).setFirstResult(page * pageSize).setMaxResults(pageSize)
                .getResultList();
        this.setOpenAndCloseValuesForPricesDateMonthly(periodsList, holderID);
        return periodsList;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PeriodPricesDTO> getMonthlyPeriodPrices(long holderID, Date from, Date to) {
        List<PeriodPricesDTO> periodsList = em.createNamedQuery("DailyPrices.getMonthlyPricesRange", PeriodPricesDTO.class)
                .setParameter("id", holderID).setParameter("dateFrom", from).setParameter("dateTo", to)
                .getResultList();
        this.setOpenAndCloseValuesForPricesDateMonthly(periodsList, holderID);
        return periodsList;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PeriodPricesDTO> getMonthlyPeriodPrices(long holderID, int page, int pageSize, Date from, Date to) {
        List<PeriodPricesDTO> periodsList = em.createNamedQuery("DailyPrices.getMonthlyPricesRange", PeriodPricesDTO.class)
                .setParameter("id", holderID).setParameter("dateFrom", from).setParameter("dateTo", to)
                .setFirstResult(page * pageSize).setMaxResults(pageSize)
                .getResultList();
        this.setOpenAndCloseValuesForPricesDateMonthly(periodsList, holderID);
        return periodsList;
    }

        @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
        public List<PeriodPricesDTO> getPricesDataRangeByID(long holderID, Date from, Date to, PricesGroupingMode mode) {
        List<PeriodPricesDTO> prices =new ArrayList<PeriodPricesDTO>();
        switch(mode){
            case DAILY :
                prices = this.getDailyPeriodPrices(holderID, from, to);
                break;
            case WEEKLY :
                prices = this.getWeeklyPeriodPrices(holderID, from, to);
                break;
            case MONTHLY :
                prices = this.getMonthlyPeriodPrices(holderID, from, to);
                break;
        }
        return prices;
    }



}
