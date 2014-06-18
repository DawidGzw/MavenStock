/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocket;

import dto.PeriodPricesDTO;
import ejb.services.ShareService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.*;
import javax.websocket.server.*;
import json.*;

/**
 *
 * @author dawid
 */
@ServerEndpoint(value = "/shareprovider", decoders = {RequestObjectDecoder.class}, encoders = {ResponseObjectEncoder.class})
public class SharesValueProvider {

    private ShareService service;



    public SharesValueProvider() {
        try {
           this.service = InitialContext.doLookup("java:module/ShareService");
        } catch (NamingException ex) {
            Logger.getLogger(SharesValueProvider.class.getName()).log(Level.SEVERE, "Cannot obtain service", ex);
        }
    }
      

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
    }

    @OnMessage
   public ResponseObject onMessage(RequestObject request, Session session) {
        List<PeriodPricesDTO> prices = service.getPricesDataRangeByID(request.getShareID(), request.getFrom(),request.getTo(), request.getMode());       
        ResponseObject response = new ResponseObject(prices, request);
        return response;
    }

}
