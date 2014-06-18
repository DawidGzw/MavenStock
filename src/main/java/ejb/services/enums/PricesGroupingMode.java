/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ejb.services.enums;

/**
 *
 * @author dawid
 */
public enum PricesGroupingMode {
    DAILY, WEEKLY, MONTHLY;
    
    public static PricesGroupingMode getModeFromString(String mode){
        return mode.equalsIgnoreCase("daily") ? 
                DAILY :
                mode.equalsIgnoreCase("weekly") ?
                WEEKLY : MONTHLY; 
    }

    @Override
    public  String toString() {
        return super.toString().toLowerCase(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
