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
public class DailyPricesDTO {
    private Long id;
    private Date date;
    private float open;
    private float minimal;
    private float maximal;
    private float close;
    private float change;
    private long sales;

    public DailyPricesDTO(Long id, Date date, float open, float minimal, float maximal, float close, float change, long sales) {
        this.id = id;
        this.date = date;
        this.open = open;
        this.minimal = minimal;
        this.maximal = maximal;
        this.close = close;
        this.change = change;
        this.sales = sales;
    }

    public DailyPricesDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
      
}
