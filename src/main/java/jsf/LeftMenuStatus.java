/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jsf;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author dawid
 */
@Named("leftMenuStatus")
@SessionScoped
public class LeftMenuStatus implements Serializable{
    private boolean menuOpen=true;

    public boolean isMenuOpen() {
        return menuOpen;
    }

    public void setMenuOpen(boolean isMenuOpen) {
        this.menuOpen = isMenuOpen;
    }
    
    public void openMenu(){
        this.menuOpen=true;
    }
    
    public void closeMenu(){
        this.menuOpen=false;
    }
    
}
