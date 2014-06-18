/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dto.data_model;

import dto.PricesHolderDTO;
import dto.ShareDTO;
import dto.StockIndexDTO;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author dawid
 * @param <E>
 */
public class StockIndexDataModel<E extends PricesHolderDTO> extends ListDataModel<E> implements SelectableDataModel<E>{


    public StockIndexDataModel(List<E> list) {
        super(list);
    }
    
    @Override
    public Object getRowKey(PricesHolderDTO t) {
       return t.getId();
    }

    @Override
    public E getRowData(String string) {
        long id = Long.parseLong(string);
        for(PricesHolderDTO s : (List<PricesHolderDTO>)this.getWrappedData()){
            if(s.equalID(id)){
                return (E) s;
            }
        }
        return null;
    }
    
}
