/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import jpa.interfaces.Transformable;
import dto.ShareDTO;
import dto.StockIndexDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author dawid
 */
@NamedQueries({
    @NamedQuery(name = "StockIndex.findAll",
            query = "SELECT s FROM StockIndex s"),
    @NamedQuery(name = "StockIndex.findByShortName",
            query = "SELECT s FROM StockIndex s WHERE s.shortName = :shortName")
})
@Table(name="STOCK_INDEX")
@Entity
public class StockIndex extends PricesHolder implements Serializable, Transformable<StockIndexDTO> {


    private String country;
    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "INDEX_SHARE",
            joinColumns = @JoinColumn(name = "INDEX_ID"),
            inverseJoinColumns = @JoinColumn(name = "SHARE_ID"))
    @OrderBy("shortName ASC")
    private List<Share> shares = new ArrayList<Share>();


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean addShareData(Share data) {
        //data.addIndexes(this);
        return shares.add(data);
    }

    public List<Share> getShares() {
        return shares;
    }

    public void setShares(List<Share> shares) {
        this.shares = shares;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StockIndex)) {
            return false;
        }
        StockIndex other = (StockIndex) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }
    
    public boolean containsShare(Share s){
        for(Share share : getShares()){
            if(share.equalsByShortName(s))
                return true;
        }
        return false;
    }
    
    public void replaceShareWithEqualShortName(Share newShare){
        for(int i=0; i<getShares().size();){
            if(getShares().get(i).equalsByShortName(newShare)){
                getShares().remove(i);
                getShares().add(i, newShare);
                return;
            }
        }
        
    }

    @Override
    public String toString() {
        return "jpa.StockIndex[ id=" + getId() + " ]";
    }

    @Override
    public StockIndexDTO transformToDTO() {
                List<ShareDTO> shares = new ArrayList<ShareDTO>();
        for(Share s: getShares()){
            shares.add(s.transformToDTO());
        }
        return new StockIndexDTO(getCountry(), shares, getId(), getName(), 
                getShortName(), getInitialPriceData(), getLastPriceData(),
        getDailyPriceData().get(getDailyPriceData().size()-1).transformToDTO());
    }

}
