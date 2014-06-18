/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dto;

import java.util.Date;

/**
 *
 * @author dawid
 */
public abstract class PricesHolderDTO {
    private long id;
    private String name;
    private String shortName;
    private Date initialPriceDate;
    private Date lastPriceDate;
    private DailyPricesDTO lastPrice;

    public PricesHolderDTO(long id, String name, String shortName, Date initialPriceDate, Date lastPriceDate, DailyPricesDTO lastPrice) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.initialPriceDate = initialPriceDate;
        this.lastPriceDate = lastPriceDate;
        this.lastPrice = lastPrice;
    }

    public PricesHolderDTO() {
    }

    public DailyPricesDTO getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(DailyPricesDTO lastPrice) {
        this.lastPrice = lastPrice;
    }

    @Override
    public String toString() {
        return  this.getShortName() + " (" + this.getName() + ")";
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Date getInitialPriceDate() {
        return initialPriceDate;
    }

    public void setInitialPriceDate(Date initialPriceDate) {
        this.initialPriceDate = initialPriceDate;
    }

    public Date getLastPriceDate() {
        return lastPriceDate;
    }

    public void setLastPriceDate(Date lastPriceDate) {
        this.lastPriceDate = lastPriceDate;
    }
    
    public boolean equalShortName(String shortName){
        return this.shortName.equals(shortName);
    }
    
    public boolean equalID(long id){
        return this.id==id;
    }
}
