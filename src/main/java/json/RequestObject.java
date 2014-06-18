/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package json;

import ejb.services.enums.PricesGroupingMode;
import java.text.SimpleDateFormat;
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
public class RequestObject {
    private long shareID;
    private Date from;
    private Date to;
    private String shareName;
    private PricesGroupingMode mode;
    private static final SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy");

    public RequestObject(long shareID, Date from, Date to, PricesGroupingMode mode, String shareName) {
        this.shareID = shareID;
        this.from = from;
        this.to = to;
        this.mode = mode;
        this.shareName = shareName;
    }

    public long getShareID() {
        return shareID;
    }

    public void setShareID(long shareID) {
        this.shareID = shareID;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public PricesGroupingMode getMode() {
        return mode;
    }

    public void setMode(PricesGroupingMode mode) {
        this.mode = mode;
    }

    
    public JSONObject toJSON(){
        try {
            JSONObject json = new JSONObject();
            json.put("shareID", this.shareID);
            json.put("mode", mode.toString());
            json.put("dateFrom", ""+ValuesFormater.dateToString(this.from));
            json.put("dateTo", ValuesFormater.dateToString(this.to));
            json.put("shareName", this.shareName);
            return json;
        } catch (JSONException ex) {
            Logger.getLogger(RequestObject.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
