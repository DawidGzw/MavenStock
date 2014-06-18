/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.services.update.entrydatafillers;


import ejb.services.update.entrydatafillers.interfaces.StockDataFiller;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import jpa.*;

/**
 *
 * @author dawid
 */
@Dependent
@RandomFiller
public class RandomGeneratedDataFiller implements StockDataFiller {

    private static final float RANGE = 0.1F;
    private static Random rand = new Random(new Date().getTime());

    private Date generateBeginDate() {
        Random rand = new Random();
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.YEAR, -rand.nextInt(5));
        cal.add(Calendar.MONTH, -rand.nextInt(12));
        cal.add(Calendar.DAY_OF_YEAR, -rand.nextInt(30));
        Date beginDate = cal.getTime();
        return beginDate;
    }

    private boolean isToday(Date d){
        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(d);
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        return thatDay.get(Calendar.YEAR) == today.get(Calendar.YEAR) && thatDay.get(Calendar.MONTH) == today.get(Calendar.MONTH) && thatDay.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }
    
    private List<DailyPrices> generateDailyPricesFromDate(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        List<DailyPrices> results = new ArrayList<DailyPrices>();
        if(!isWorkingDay(cal))
            moveToNextWorkingDay(cal);
        if(cal.getTime().after(new Date()))
            return results;
        results.add(this.generateFirstPrice(d));
        for (int i=0; !isToday(cal.getTime()); i++){
            moveToNextWorkingDay(cal);
            results.add(this.generateDailyPrice(cal.getTime(), results.get(i)));
        }
        return results;
    }
    
    private boolean isWorkingDay(Calendar cal){
        return cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY && cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY;
    }
    
    private void moveToNextWorkingDay(Calendar cal) {
        cal.add(Calendar.DAY_OF_YEAR, 1);
        while(!isWorkingDay(cal)){
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private DailyPrices generateDailyValues(Date d, float openPrice, boolean positiveSignOfPriceChange, boolean positiveSignOfSalesCHange){
        float closePriceChangevalue = rand.nextFloat() * RANGE * openPrice;
        float closePrice = positiveSignOfPriceChange ? (openPrice + closePriceChangevalue) : (openPrice - closePriceChangevalue);
        closePrice = Math.abs(closePrice);
        float change = ((closePrice - openPrice)/openPrice) *100;
        float maxPrice = rand.nextFloat() * RANGE * openPrice + Math.max(openPrice, closePrice);
        float minPrice = -rand.nextFloat() * RANGE * openPrice + Math.min(openPrice, closePrice);
        long sales = rand.nextInt(10000000);
        sales = positiveSignOfSalesCHange ? rand.nextInt(100000) + sales : -rand.nextInt(100000) + sales;
        sales = Math.abs(sales);
        return new DailyPrices(d, this.round(openPrice), this.round(minPrice), this.round(maxPrice), this.round(closePrice), this.round(change), sales);
    }
    
    private DailyPrices generateFirstPrice(Date d) {
        float openPrice = rand.nextInt(200);
        boolean positiveSignOfPriceChange = rand.nextBoolean();
        boolean positiveSignOfSalesCHange = rand.nextBoolean();
        return this.generateDailyValues(d, openPrice, positiveSignOfPriceChange, positiveSignOfSalesCHange);

    }
    
    private float round(float value){
        return Math.round(value * 100)/100F;
    }

    private DailyPrices generateDailyPrice(Date date, DailyPrices previous) {
        int openChange = rand.nextBoolean() ? rand.nextBoolean() ? 1 : -1 : 0;
        boolean positiveSignOfPriceChange = rand.nextBoolean();
        boolean positiveSignOfSalesCHange = rand.nextBoolean();
        float openPrice = openChange==1 ? previous.getClose() + rand.nextFloat()*RANGE*previous.getClose() : 
                openChange==-1 ?  previous.getClose() - rand.nextFloat()*RANGE*previous.getClose() : 
                previous.getClose();
        openPrice = Math.abs(openPrice);
        return this.generateDailyValues(date, openPrice, positiveSignOfPriceChange, positiveSignOfSalesCHange);

    }

    @Override
    public void fillDataInIndex(StockIndex index) {
        Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, "filling random data for index: "+index.getName());
        this.fillShareData(index);
        for(Share s : index.getShares()){
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, "filling random data for share: "+s.getName());
            this.fillShareData(s);
        }
    }

    @Override
    public void fillShareData(PricesHolder shareData) {
        Date beginDate = shareData.getLastPriceData()!=null ? shareData.getLastPriceData() : this.generateBeginDate();
        shareData.addDailyDataCollection(this.generateDailyPricesFromDate(beginDate));
    }

}
