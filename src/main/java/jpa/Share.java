/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import jpa.interfaces.Transformable;
import dto.ShareDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author dawid
 */
@NamedQueries({
    @NamedQuery(name = "Share.findAll",
            query = "SELECT s FROM Share s"),
    @NamedQuery(name = "Share.findByShortName",
            query = "SELECT s FROM Share s WHERE s.shortName = :shortName")
})
@Entity
public class Share extends PricesHolder implements Serializable, Transformable<ShareDTO> {

    @ManyToMany(mappedBy = "shares")
    private List<StockIndex> indexes = new ArrayList<StockIndex>();

    public List<StockIndex> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<StockIndex> indexes) {
        this.indexes = indexes;
    }

    public boolean addIndex(StockIndex index) {
        return indexes.add(index);
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
        if (!(object instanceof Share)) {
            return false;
        }
        Share other = (Share) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "jpa.Share[ id=" + getId() + " ]";
    }
    

    @Override
    public ShareDTO transformToDTO() {
        return new ShareDTO(getId(), getName(), getShortName(),
                getInitialPriceData(), getLastPriceData(), 
                getDailyPriceData().get(getDailyPriceData().size()-1).transformToDTO());
    }
}
