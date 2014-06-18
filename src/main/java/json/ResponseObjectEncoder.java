/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author dawid
 */
public class ResponseObjectEncoder implements Encoder.Text<ResponseObject> {

    @Override
    public String encode(ResponseObject object) throws EncodeException {
        try {
            JSONObject response = object.toJSON();
            System.out.println(response.toString());
            return response.toString();
        } catch (JSONException ex) {
            Logger.getLogger(ResponseObjectEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

}
