/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author dawid
 */
@SessionScoped
@Named("requestColorHolder")
public class ColorHolder implements Serializable{

    private String backgroundColor;
    private String textColor;
    private String chartColor;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getChartColor() {
        return chartColor;
    }

    public void setChartColor(String chartColor) {
        this.chartColor = chartColor;
    }

}
