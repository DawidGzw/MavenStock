/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.services.update.entrydatafillers.interfaces;

import java.io.IOException;
import jpa.*;

/**
 *
 * @author dawid
 */
public interface StockDataFiller {

    void fillDataInIndex(StockIndex index) throws IOException;

    void fillShareData(PricesHolder shareData) throws IOException;
}
