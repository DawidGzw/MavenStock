/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author dawid
 */

@NamedQueries({
    @NamedQuery(name = "PricesHolder.findAll",
            query = "SELECT e FROM PricesHolder s JOIN s.dailyPriceData e "
        + "WHERE s.id = :id "
        + "ORDER BY e.date ASC "),
    @NamedQuery(name = "PricesHolder.findAllRange",
            query = "SELECT e FROM PricesHolder s JOIN s.dailyPriceData e "
        + "WHERE s.id = :id AND e.date BETWEEN :dateFrom AND :dateTo "
        + "ORDER BY e.date ASC "),
})
@Table(name = "PRICES_HOLDER")
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public  class PricesHolder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String shortName;
    @OneToMany(mappedBy = "priceHolder", cascade = CascadeType.ALL)
    @OrderBy("date ASC")
    private List<DailyPrices> dailyPriceData = new ArrayList<DailyPrices>();
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date initialPriceData;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date lastPriceData;

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

    public Date getInitialPriceData() {
        return initialPriceData;
    }

    public Date getLastPriceData() {
        return lastPriceData;
    }

    private void checkDatesPresence(Date date) {
        if (this.initialPriceData == null) {
            this.initialPriceData = date;
        }
        if (this.lastPriceData == null) {
            this.lastPriceData = date;
        }
    }

    private void updateDateRange(Date date) {
        if (this.initialPriceData.after(date)) {
            this.initialPriceData = date;
        } else if (this.lastPriceData.before(date)) {
            this.lastPriceData = date;
        }
    }

    private void inspectShareDailyData(Date date) {
        this.checkDatesPresence(date);
        this.updateDateRange(date);
    }

    public boolean addDailyData(DailyPrices data) {
        data.setPriceHolder(this);
        this.inspectShareDailyData(data.getDate());
        return this.dailyPriceData.add(data);

    }
    
    public boolean addDailyDataCollection(Collection<DailyPrices> data){
        for(DailyPrices d : data){
            d.setPriceHolder(this);
            this.inspectShareDailyData(d.getDate());
        }
        return this.dailyPriceData.addAll(data);
    }

    public List<DailyPrices> getDailyPriceData() {
        return dailyPriceData;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Name : " + this.getName() + " short : " + this.getShortName() + "\n\n");
        for (DailyPrices d : this.getDailyPriceData()) {
            builder.append(d.toString());
            builder.append("\n");
        }
        return builder.toString();
    }

    public int sizeOfDailyPriceData() {
        return this.getDailyPriceData().size();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
        
    public boolean equalsByShortName(PricesHolder s){
        if(this.getShortName().equals(s.getShortName()))
            return true;
        return false;
    }
}
