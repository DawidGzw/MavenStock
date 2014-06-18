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
public class ShareDTO extends PricesHolderDTO{

    public ShareDTO(long id, String name, String shortName, Date initialPriceDate, Date lastPriceDate, DailyPricesDTO lastPrice) {
        super(id, name, shortName, initialPriceDate, lastPriceDate, lastPrice);
    }

    public ShareDTO() {
    }
    
}
