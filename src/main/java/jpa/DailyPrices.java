/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import jpa.interfaces.Transformable;
import dto.DailyPricesDTO;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author dawid
 */
@NamedQueries({

    @NamedQuery(name = "DailyPrices.getWeeklyPrices",
            query = "SELECT NEW dto.PeriodPricesDTO(MIN(d.date), MAX(d.date), MIN(d.minimal), MAX(d.maximal), SUM(d.sales)) "
            + "FROM PricesHolder h JOIN h.dailyPriceData d "
            + "WHERE h.id = :id "
            + "GROUP BY d.weekNumber "
            + "ORDER BY d.weekNumber ASC "),
    @NamedQuery(name = "DailyPrices.getMonthlyPrices",
            query = "SELECT NEW dto.PeriodPricesDTO(MIN(d.date), MAX(d.date), MIN(d.minimal), MAX(d.maximal), SUM(d.sales)) "
            + "FROM PricesHolder h JOIN h.dailyPriceData d "
            + "WHERE h.id = :id "
            + "GROUP BY d.monthNumber "
            + "ORDER BY d.monthNumber ASC "),
    @NamedQuery(name = "DailyPrices.getDailyPrices",
            query = "SELECT NEW dto.PeriodPricesDTO(d.date, d.date, d.open, d.minimal, d.maximal, d.close,  d.change, d.sales) "
            + "FROM PricesHolder h JOIN h.dailyPriceData d "
            + "WHERE h.id = :id "
            + "ORDER BY d.date ASC "),
    @NamedQuery(name = "DailyPrices.getWeeklyPricesRange",
            query = "SELECT NEW dto.PeriodPricesDTO(MIN(d.date), MAX(d.date), MIN(d.minimal), MAX(d.maximal), SUM(d.sales)) "
            + "FROM PricesHolder h JOIN h.dailyPriceData d "
            + "WHERE h.id = :id AND d.date BETWEEN :dateFrom AND :dateTo "
            + "GROUP BY d.weekNumber "
            + "ORDER BY d.weekNumber ASC "),
    @NamedQuery(name = "DailyPrices.getMonthlyPricesRange",
            query = "SELECT NEW dto.PeriodPricesDTO(MIN(d.date), MAX(d.date), MIN(d.minimal), MAX(d.maximal),SUM(d.sales)) "
            + "FROM PricesHolder h JOIN h.dailyPriceData d "
            + "WHERE h.id = :id AND d.date BETWEEN :dateFrom AND :dateTo "
            + "GROUP BY d.monthNumber "
            + "ORDER BY d.monthNumber ASC "),
    @NamedQuery(name = "DailyPrices.getDailyPricesRange",
            query = "SELECT NEW dto.PeriodPricesDTO(d.date, d.date, d.open, d.minimal, d.maximal, d.close,  d.change, d.sales) "
            + "FROM PricesHolder h JOIN h.dailyPriceData d "
            + "WHERE h.id = :id AND d.date BETWEEN :dateFrom AND :dateTo "
            + "ORDER BY d.date ASC ")
})
@Table(name = "DAILY_PRICES")
@Entity
public class DailyPrices implements Serializable, Transformable<DailyPricesDTO> {

    private static final long serialVersionUID = 1L;
    private static final int firstYear = 1970;
    private static final int upperWeeksInYearLimit = 53;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "HOLDER_ID")
    private PricesHolder priceHolder;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date date;
    @Column(name = "OPEN_PRICE")
    private float open;
    private float minimal;
    private float maximal;
    @Column(name = "CLOSE_PRICE")
    private float close;
    private float change;
    private long sales;
    @Column(name = "MONTH_NUMBER")
    private int monthNumber;
    @Column(name = "WEEK_NUMBER")
    private int weekNumber;

    public DailyPrices() {
    }

    public DailyPrices(Date date, float open, float minimal, float maximal, float close, float change, long sales) {
        this.date = date;
        this.open = open;
        this.minimal = minimal;
        this.maximal = maximal;
        this.close = close;
        this.change = change;
        this.sales = sales;
        this.setDate(date);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PricesHolder getPriceHolder() {
        return priceHolder;
    }

    public void setPriceHolder(PricesHolder priceHolder) {
        this.priceHolder = priceHolder;
    }

    public Date getDate() {
        return date;
    }

    public final void setDate(Date date) {
        this.date = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int yearsFactor = cal.get(Calendar.YEAR) - firstYear;
   
        this.setMonthNumber(yearsFactor * 12 + cal.get(Calendar.MONTH));       
        if(cal.get(Calendar.WEEK_OF_YEAR) == 1 && cal.get(Calendar.MONTH)==11)
            yearsFactor++;  
        this.setWeekNumber(yearsFactor * upperWeeksInYearLimit + cal.get(Calendar.WEEK_OF_YEAR));
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getMinimal() {
        return minimal;
    }

    public void setMinimal(float minimal) {
        this.minimal = minimal;
    }

    public float getMaximal() {
        return maximal;
    }

    public void setMaximal(float maximal) {
        this.maximal = maximal;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public float getChange() {
        return change;
    }

    public void setChange(float change) {
        this.change = change;
    }

    public long getSales() {
        return sales;
    }

    public void setSales(long sales) {
        this.sales = sales;
    }


    public int getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("date: " + date + "\n");
        str.append("open: " + open + "\n");
        str.append("minimal: " + minimal + "\n");
        str.append("maximal: " + maximal + "\n");
        str.append("close: " + close + "\n");
        str.append("change: " + change + "\n");
        str.append("sales: " + sales + "\n");
        return str.toString();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DailyPrices)) {
            return false;
        }
        DailyPrices other = (DailyPrices) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public DailyPricesDTO transformToDTO() {
        return new DailyPricesDTO(this.id, this.date, this.open, this.minimal, this.maximal, this.close, this.change, this.sales);
    }

}
