/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author dawid
 */
@Named(value = "loginBean")
@RequestScoped
public class LoginBean {

    /**
     * Creates a new instance of LoginBean
     */
    private static Map<String, String> logins = new HashMap<String, String>();

    static {
        logins.put("pepek", "anomalia");
        System.out.println("created");
    }
    private String username = "";
    private String password = "";
    @Inject
    private ClientBackingBean backBean;

    public LoginBean() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void processLogin() {
        String pass = logins.get(username);
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
        if (pass == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("main.login.userWrong"), null));
        } else if (!pass.equals(password)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("main.login.passWrong"), null));
        } else {
            backBean.setUserName(username);
        }
    }

    public void processLogout() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
        backBean.setUserName(null);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("main.logout.successful"), null));
    }
    
}
