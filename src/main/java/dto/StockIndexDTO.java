/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dto;

import dto.data_model.StockIndexDataModel;
import java.util.Date;
import java.util.List;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author dawid
 */
public class StockIndexDTO extends PricesHolderDTO{
    private String country;
    private List<ShareDTO> shares;
    
    
    public StockIndexDTO(String country, List<ShareDTO> shares, long id, String name, String shortName, Date initialPriceDate, Date lastPriceDate, DailyPricesDTO lastPrice) {
        super(id, name, shortName, initialPriceDate, lastPriceDate, lastPrice);
        this.country = country;
        this.shares = shares;
    }

    public StockIndexDTO() {
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<ShareDTO> getShares() {
        return shares;
    }

    public void setShares(List<ShareDTO> shares) {
        this.shares = shares;
    }
    
    public ShareDTO getShareWithEqualShortName(String shortName){
        ShareDTO share = null;
        for(ShareDTO s : getShares()){
            if(s.equalShortName(shortName)){
                share=s;
                break;
            }
        }
        return share;
    }

    public StockIndexDataModel getDataModel(){
        return new StockIndexDataModel(this.shares);
    }
    
}
