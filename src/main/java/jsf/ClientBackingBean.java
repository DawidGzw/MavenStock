/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf;

import dto.PricesHolderDTO;
import dto.ShareDTO;
import dto.StockIndexDTO;
import ejb.services.ShareService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.enterprise.context.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import utils.formatter.ValuesFormater;

/**
 *
 * @author dawid
 */
@Named(value = "clientBean")
@SessionScoped
public class ClientBackingBean implements Serializable {

    @Inject
    private ShareService service;

    private String userName;
    private PricesHolderDTO choosedShare;
    private StockIndexDTO choosedIndex;
    private Map<Long, StockIndexDTO> allIndexes = new HashMap<Long, StockIndexDTO>();
    private Date from;
    private Date to;

    public String getUserName() {
        return userName;
    }

    public ShareService getService() {
        return service;
    }

    public Collection<StockIndexDTO> getAvailableIndexes() {
        Collection<StockIndexDTO> indexes = null;
        if (this.allIndexes.isEmpty()) {
            indexes = service.getAllDTOStockIndexes();
            initializeIndexesMap(indexes);
        } else {
            //indexes = this.allIndexes.values();
            indexes = new ArrayList<StockIndexDTO>(this.allIndexes.values());
        }
        return indexes;
    }

    private void initializeIndexesMap(Collection<StockIndexDTO> indexes) {
        if (this.allIndexes.isEmpty()) {
            for (StockIndexDTO i : indexes) {
                this.allIndexes.put(i.getId(), i);
            }
        }
    }

    public Map<Long, String> getAvailableIndexesMap() {
        Collection<StockIndexDTO> indexes = getAvailableIndexes();
        Map<Long, String> indexesToShow = new LinkedHashMap<Long, String>();
        if (this.choosedIndex == null) {
            this.putNullItemToMap(indexesToShow);
        } else {
            indexesToShow.put(this.choosedIndex.getId(), this.choosedIndex.toString());
        }
        for (StockIndexDTO i : indexes) {
            if (this.choosedIndex == null || this.choosedIndex.getId() != i.getId()) {
                indexesToShow.put(i.getId(), i.toString());
            }
        }
        return indexesToShow;
    }

    public Map<Long, String> getAvailableSharesMap() {
        Map<Long, String> stringShares = new LinkedHashMap<Long, String>();
        if (this.choosedShare == null) {
            this.putNullItemToMap(stringShares);
            if (this.choosedIndex == null) {
                return stringShares;
            }
        } else {
            stringShares.put(this.choosedShare.getId(), this.choosedShare.toString());
        }
        if (this.choosedShare == null || this.choosedIndex.getId() != this.choosedShare.getId()) {
            stringShares.put(this.choosedIndex.getId(), this.choosedIndex.toString());
        }
        for (ShareDTO i : this.choosedIndex.getShares()) {
            if (this.choosedShare == null || this.choosedShare.getId() != i.getId()) {
                stringShares.put(i.getId(), i.toString());
            }
        }
        return stringShares;
    }

    private void putNullItemToMap(Map<Long, String> map) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
        map.put(null, bundle.getString("select.one"));
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getLoggedUser() {
        return userName != null;
    }

    void updateShareSelection(PricesHolderDTO s) {
        this.choosedShare = s;
    }

    public void setChoosedShare(String choosedShare) {
        try {
            long id = Long.parseLong(choosedShare);
            if (this.choosedIndex.getId() == id) {
                this.choosedShare = this.choosedIndex;
                return;
            }
            for (PricesHolderDTO s : this.choosedIndex.getShares()) {
                if (s.getId() == id) {
                    this.choosedShare = s;
                    return;
                }
            }
            this.choosedShare = null;
        } catch (NumberFormatException ex) {
            this.choosedShare = null;
        }
        this.clearDates();
    }

    public void setChoosedIndex(String choosedIndex) {
        try {
            this.choosedIndex = this.allIndexes.get(Long.parseLong(choosedIndex));
        } catch (NumberFormatException ex) {
            this.choosedIndex = null;
        }
        this.clearDates();
    }

    public String getChoosedShare() {
        return choosedShare == null ? null : this.choosedShare.toString();
    }

    public String getChoosedIndex() {
        return choosedIndex == null ? null : this.choosedIndex.toString();
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {

        if ((this.to == null || from.before(this.to)) || from.equals(this.to)) {
            this.from = from;
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
            context.addMessage("chartsForm:dateFromCal", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("chartWebsite.fieldset.dateFrom.wrongDate"), null));
        }
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        if ((this.from == null || from.before(to)) || from.equals(to)) {
            this.to = to;
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
            context.addMessage("chartsForm:dateToCal", new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("chartWebsite.fieldset.dateTo.wrongDate"), null));
        }
    }

    public String getChoosedShareDateRangeFrom() {
        if (choosedShare == null) {
            return "01-01-1980";
        } else {
            return ValuesFormater.dateToString(choosedShare.getInitialPriceDate());
        }

    }

    public String getChoosedShareDateRangeTo() {
        if (choosedShare == null) {
            return ValuesFormater.dateToString(new Date());
        } else {
            return ValuesFormater.dateToString(choosedShare.getLastPriceDate());
        }
    }

    public void clearDates() {
        this.from = null;
        this.to = null;
    }

}
