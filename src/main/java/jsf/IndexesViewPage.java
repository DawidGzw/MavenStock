/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf;

import dto.PricesHolderDTO;
import java.util.ResourceBundle;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author dawid
 */
@Named("indexViewPage")
@RequestScoped
public class IndexesViewPage {

    @Inject
    private ClientBackingBean clientBean;
    private PricesHolderDTO selectedPriceHolder;

    public PricesHolderDTO getSelectedPriceHolder() {
        return selectedPriceHolder;
    }

    public void setSelectedPriceHolder(PricesHolderDTO selectedPriceHolder) {
        this.selectedPriceHolder = selectedPriceHolder;
    }
    
    public String goToChartPageForIndex(String indexId){
        clientBean.setChoosedIndex(indexId);
        clientBean.setChoosedShare(indexId);
        return "chartWebsite";
    }

    public String goToChartPage(String indexId) {
        if (selectedPriceHolder == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("index.share.view.error"), null));
            return "";
        } else {
            clientBean.setChoosedIndex(indexId);
            clientBean.updateShareSelection(this.selectedPriceHolder);
        }
        return "chartWebsite";
    }
}
