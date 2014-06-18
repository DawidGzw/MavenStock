/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json;

import dto.PeriodPricesDTO;
import java.util.List;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author dawid
 */
public final class ResponseObject {

    private List<PeriodPricesDTO> prices;
    private RequestObject request;
    private float maxValue;
    private float minValue;
    private float maxMidValue;
    private float minMidValue;
    private long minSales;
    private long maxSales;

    public ResponseObject() {
    }

    public ResponseObject(List<PeriodPricesDTO> prices, RequestObject request) {
        this.request = request;
        this.setPrices(prices);
    }

    public void setPrices(List<PeriodPricesDTO> prices) {
        this.prices = prices;
        if (prices.size() > 0) {
            this.setUpInitialValues(prices.get(0));
        }
        findMaxMinValues(prices);
    }

    private void setUpInitialValues(PeriodPricesDTO firstPrice) {
        this.minValue = firstPrice.getMinimal();
        this.maxValue = firstPrice.getMaximal();
        this.maxSales = firstPrice.getSales();
        this.minSales = firstPrice.getSales();
        this.maxMidValue = firstPrice.getClose();
        this.minMidValue = firstPrice.getClose();
    }

    private void findMaxMinValues(List<PeriodPricesDTO> prices) {
        for (int i = 1; prices.size() > i; i++) {
            PeriodPricesDTO price = prices.get(i);
            if (this.minValue > price.getMinimal()) {
                this.minValue = price.getMinimal();
            }
            if (this.maxValue < price.getMaximal()) {
                this.maxValue = price.getMaximal();
            }
            if (this.maxMidValue < price.getClose()) {
                this.maxMidValue = price.getClose();
            }
            if (this.minMidValue > price.getClose()) {
                this.minMidValue = price.getClose();
            }
            if (this.maxSales < price.getSales()) {
                this.maxSales = price.getSales();
            }
            if (this.minSales > price.getSales()) {
                this.minSales = price.getSales();
            }
        }
    }

    public void setRequest(RequestObject request) {
        this.request = request;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject response = new JSONObject();
        response.put("minAllTime", ""+this.minValue);
        response.put("maxAllTime", ""+this.maxValue);
        response.put("maxMidTime", ""+this.maxMidValue);
        response.put("minMidTime", ""+this.minMidValue);
        response.put("maxSales", ""+this.maxSales);
        response.put("minSales", ""+this.minSales);
        for (PeriodPricesDTO shareVal : this.prices) {
            response.append("shareValues", shareVal.toJSON());
        }
        JSONObject request = this.request.toJSON();
        response.putOnce("request", request);
        return response;
    }

}
