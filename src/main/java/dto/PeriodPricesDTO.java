/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import utils.formatter.ValuesFormater;

/**
 *
 * @author dawid
 */
public class PeriodPricesDTO {

    private Date beginDate;
    private Date endDate;
    private float open;
    private float minimal;
    private float maximal;
    private float close;
    private float change;
    private long sales;

    public PeriodPricesDTO() {
    }

    public PeriodPricesDTO(Date beginDate, Date endDate, float open, float minimal, float maximal, float close, float change, long sales) {
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.open = open;
        this.minimal = minimal;
        this.maximal = maximal;
        this.close = close;
        this.change = change;
        this.sales = sales;
    }


    public PeriodPricesDTO(Date beginDate, Date endDate, float minimal, float maximal, long sales) {
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.minimal = minimal;
        this.maximal = maximal;
        this.sales = sales;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    @Override
    public String toString() {
        return "PeriodPrices{" + "beginDate=" + beginDate + ", endDate=" + endDate + ", open=" + open + ", minimal=" + minimal + ", maximal=" + maximal + ", close=" + close + ", change=" + change + ", sales=" + sales + '}';
    }

    public Object toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("openValue", ""+this.open);
            json.put("closeValue", ""+this.close);
            json.put("maxValue", ""+this.maximal);
            json.put("minValue", ""+this.minimal);
            json.put("salesValue", ""+this.sales);
            json.put("begin", ValuesFormater.dateToString(this.beginDate));
            json.put("end", ValuesFormater.dateToString(this.endDate));
        } catch (JSONException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "PeriodPricesDTO Object is not convertable into JSON");
        }
        return json;
    }

}
