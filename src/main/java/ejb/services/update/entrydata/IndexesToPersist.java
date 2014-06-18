/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ejb.services.update.entrydata;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.context.Dependent;
import jpa.Share;
import jpa.StockIndex;

/**
 *
 * @author dawid
 */
@Singleton
@Dependent
public class IndexesToPersist {
    
    private List<StockIndex> indexes = new ArrayList<StockIndex>();
    private String[][] wig20ShareNames =  {{"Alior Bank","ALR"},{"LW Bogdanka","LWB"}
    ,{"BZ WBK","BZW"},{"Eurocash","EUR"},{"Grupa Lotos","LTS"},{"Jastrzębska Spółka węglowa","JSW"}
    ,{"Kernel Holding","KER"},{"KGHM","KGH"},{"LPP","LPP"},{"mBank","MBK"}
    ,{"Orange Polska","OPL"},{"Bank Pekao","PEO"},{"PGE Polska Grupa Energetyczna","PGE"},{"PGNiG","PGN"}
    ,{"PKN Orlen","PKN"},{"PKO BP","PKO"},{"PZU","PZU"},{"Synthos","SNS"}
    ,{"Tauron Polska Energia","TPE"},{"Asseco Poland","ACP"}};
    
    @PostConstruct
    public void init(){
        StockIndex wig20 = new StockIndex();
        wig20.setCountry("Poland");
        wig20.setName("WIG 20");
        wig20.setShortName("WIG20");
        for(int i=0; i< 20; i++){
           Share share = new Share();
           share.setName(wig20ShareNames[i][0]);
           share.setShortName(wig20ShareNames[i][1]);
           wig20.addShareData(share);
        }
        indexes.add(wig20);
    }
    
    public List<StockIndex> getStockIndexes(){
        return this.indexes;
    }
    
}
