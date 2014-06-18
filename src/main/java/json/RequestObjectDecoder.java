/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import ejb.services.enums.PricesGroupingMode;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import utils.formatter.ValuesFormater;

/**
 *
 * @author dawid
 */
public class RequestObjectDecoder implements Decoder.Text<RequestObject> {

    @Override
    public RequestObject decode(String string) throws DecodeException {
        JsonObject jsonObject = Json.createReader(new StringReader(string)).readObject();
        try {
            return new RequestObject(Long.parseLong(jsonObject.getString("shareID")),
                    ValuesFormater.stringToDate(jsonObject.getString("dateFrom")),
                    ValuesFormater.stringToDate(jsonObject.getString("dateTo")) ,
                    PricesGroupingMode.getModeFromString(jsonObject.getString("mode")),
                    jsonObject.getString("shareName"));
        } catch (ParseException ex) {
            Logger.getLogger(RequestObjectDecoder.class.getName()).log(Level.SEVERE, "Decoding request JSON Object has failed", ex);
        }
        return null;
    }

    @Override
    public boolean willDecode(String string) {
        try {
            Json.createReader(new StringReader(string)).readObject();
            return true;
        } catch (JsonException ex) {
            return false;
        }
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

}
